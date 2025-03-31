// CustomLogoutSuccessHandler.java
package com.example.security;

import com.example.service.AuthService;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final AuthService authService;

    public CustomLogoutSuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        // Grab the SSO cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ssoTicket".equals(cookie.getName())) {
                    authService.invalidateTicket(cookie.getValue()); // delete from DB
                    break;
                }
            }
        }

        // Redirect to login with logout message
        response.sendRedirect("/login?logout");
    }
}
