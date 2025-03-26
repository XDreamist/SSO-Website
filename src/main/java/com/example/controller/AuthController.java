package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.service.TicketService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String welcome(@AuthenticationPrincipal LdapUserDetails user, Model model, HttpServletResponse response) {
        // if (user != null) {
        //     String username = user.getUsername();
        //     model.addAttribute("username", username);
        //     // Generate SSO ticket only if not already authenticated via ticket
        //     if (SecurityContextHolder.getContext().getAuthentication().getCredentials() == null) {
        //         String ticket = ticketService.generateTicket(username);
        //         Cookie ticketCookie = new Cookie("ssoTicket", ticket);
        //         ticketCookie.setHttpOnly(true);
        //         ticketCookie.setPath("/");
        //         ticketCookie.setMaxAge((int) (TICKET_EXPIRY_MINUTES * 60));
        //         response.addCookie(ticketCookie);
        //     }
        // }
        return "index";
    }
}