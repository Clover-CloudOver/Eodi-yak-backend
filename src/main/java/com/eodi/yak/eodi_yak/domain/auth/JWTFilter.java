package com.eodi.yak.eodi_yak.domain.auth;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collections;
import java.util.Enumeration;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, java.io.IOException {
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더 검증
        if(authorization == null || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        // token 소멸 시간 검증
        if(jwtUtil.isExpired(token)){
            System.out.println("Token Expired");
            filterChain.doFilter(request, response);
            return;
        }


        // 사용자 ID 추출
        String memberId = jwtUtil.getLoginId(token);

        // 요청 수정: 헤더에 사용자 ID 추가 후, 새로운 요청으로 교체
        HttpServletRequest modifiedRequest = new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("Authorization".equals(name)) {
                    return "Bearer " + token;  // Authorization 헤더를 그대로 사용
                }
                return super.getHeader(name);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                if ("X-USER-ID".equals(name)) {
                    return Collections.enumeration(Collections.singletonList(memberId));  // X-USER-ID 헤더 추가
                }
                return super.getHeaders(name);
            }
        };

        // 수정된 요청을 filterChain에 전달
        filterChain.doFilter(modifiedRequest, response);
    }
}