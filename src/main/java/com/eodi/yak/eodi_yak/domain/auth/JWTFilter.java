package com.eodi.yak.eodi_yak.domain.auth;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

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

        filterChain.doFilter(request, response);
    }

    }