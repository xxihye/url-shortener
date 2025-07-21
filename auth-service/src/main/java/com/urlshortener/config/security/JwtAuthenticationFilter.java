package com.urlshortener.config.security;

import com.urlshortener.auth.token.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = tokenProvider.resolveToken(request);

        if (token == null) {
            log.debug("요청에 JWT 토큰이 없습니다. 인증 없이 처리됩니다. URI: {}", request.getRequestURI());
        } else if (!tokenProvider.validateToken(token)) {
            log.warn("유효하지 않은 JWT 토큰입니다. URI: {}", request.getRequestURI());
        } else {
            SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));
        }

        // 토큰이 없거나 유효하지 않으면 인증 없이 넘어감, 이후 401 처리됨
        filterChain.doFilter(request, response);
    }
}
