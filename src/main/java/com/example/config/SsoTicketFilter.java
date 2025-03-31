// SsoTicketFilter.java
package com.example.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.model.Ticket;
import com.example.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SsoTicketFilter extends OncePerRequestFilter {

    private final AuthService authService;

    public SsoTicketFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
    
        String requestUri = request.getRequestURI();
    
        // 🔹 1. If user is NOT authenticated, check for auto-login
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (requestUri.equals("/login") || requestUri.equals("/home")) {
                Optional<Ticket> ticketOpt = authService.getAnyValidTicket();
    
                if (ticketOpt.isPresent()) {
                    Ticket ticket = ticketOpt.get();
    
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            ticket.getUsername(), null, Collections.emptyList());
    
                    SecurityContextHolder.getContext().setAuthentication(auth);
    
                    HttpSession session = request.getSession(true);
                    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    
                    if (requestUri.equals("/login")) {
                        long secondsRemaining = java.time.Duration.between(LocalDateTime.now(), ticket.getExpiresAt()).getSeconds();
                        int maxAge = (int) Math.max(0, secondsRemaining);
    
                        Cookie ssoCookie = new Cookie("ssoTicket", ticket.getToken());
                        ssoCookie.setPath("/");
                        ssoCookie.setMaxAge(maxAge);
                        ssoCookie.setHttpOnly(true);
                        response.addCookie(ssoCookie);
    
                        response.sendRedirect("/home");
                        return;
                    }
                } else {
                    // No ticket in DB → only redirect if not on login page
                    if (!requestUri.equals("/login")) {
                        response.sendRedirect("/login");
                        return;
                    }
                }
            }
    
        } else {
            // 🔹 2. If user IS authenticated, check if their ticket still exists in DB
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
    
            boolean validTicketExists = authService.hasValidTicketForUser(username);
    
            if (!validTicketExists && requestUri.equals("/home")) {
                // Ticket was deleted (logout from other browser) → force logout
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
                response.sendRedirect("/login?expired");
                return;
            }
        }
    
        filterChain.doFilter(request, response);
    }
}    