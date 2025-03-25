package com.eodi.yak.eodi_yak.global.config;

import com.eodi.yak.eodi_yak.domain.auth.JWTFilter;
import com.eodi.yak.eodi_yak.domain.auth.JWTUtil;
import com.eodi.yak.eodi_yak.domain.auth.LoginFilter;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration configuration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:3000");  // 로컬 환경
        corsConfiguration.addAllowedOrigin("http://127.0.0.1:3000");  // 로컬 환경
        //corsConfiguration.addAllowedOrigin("*");
        //corsConfiguration.addAllowedOrigin("http://eodi-yak.local:32050");
        corsConfiguration.addAllowedOrigin(allowedOrigin);
        corsConfiguration.addAllowedMethod("GET");  // 허용할 HTTP 메서드
        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("PUT");
        corsConfiguration.addAllowedMethod("DELETE");
        corsConfiguration.addAllowedMethod("OPTIONS");
        corsConfiguration.addAllowedHeader("*");  // 모든 헤더 허용
        corsConfiguration.setAllowCredentials(true);  // 쿠키 전송 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);  // 모든 요청에 대해 CORS 적용

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 필요에 따라 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/**", "/api/v1/**", "/api-docs/**", "/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // OPTIONS 메서드를 명시적으로 허용
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS 설정
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()));  // H2 콘솔 접근을 위해 헤더 설정
        http
                .sessionManagement((session)->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 새로 만든 로그인 필터를 원래의 (UsernamePasswordAuthenticationFilter)의 자리에 넣음
        http
                .addFilterAt(new LoginFilter(authenticationManager(configuration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 로그인 필터 이전에 JWTFilter를 넣음
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);


        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader(){
        return new BasicGrpcAuthenticationReader();
    }
}
