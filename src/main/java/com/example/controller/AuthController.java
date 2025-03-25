package com.example.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "Please log in with your Active Directory credentials";
    }

    @GetMapping("/welcome")
    public String welcome(@AuthenticationPrincipal LdapUserDetails user) {
        return "Welcome, " + user.getUsername() + "! You are authenticated via Active Directory.";
    }
}