package com.eodi.yak.eodi_yak.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;

@OpenAPIDefinition(
        info = @Info(
                title = "어디약?! API 명세서",
                description = "어디약?! Backend API 명세서입니다.",
                version = "v1"
        )
)
public class SwaggerConfig {
    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    public OpenAPI openAPI() {
        String securityJwtName = "JWT";
        // 보안 요구사항 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);

        // JWT 인증 스키마 정의
        Components components = new Components()
                .addSecuritySchemes(securityJwtName, new SecurityScheme()
                        .name(securityJwtName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer") // 'Bearer'를 설정
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER) // 헤더에서 JWT를 받도록 설정
                        .description("JWT Token"));

        return new OpenAPI()
                .addSecurityItem(securityRequirement) // 보안 요구 사항 추가
                .components(components); // 컴포넌트 추가
    }
}
