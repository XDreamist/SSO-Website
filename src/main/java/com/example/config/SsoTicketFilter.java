package com.example.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SsoTicketFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final AntPathRequestMatcher loginMatcher = new AntPathRequestMatcher("/login");

    public SsoTicketFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Skip filter for login page to avoid redirect loop
        if (loginMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ssoTicket".equals(cookie.getName())) {
                    String ticket = cookie.getValue();
                    if (authService.validateTicket(ticket)) {
                        response.sendRedirect("/home");
                        return;
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}