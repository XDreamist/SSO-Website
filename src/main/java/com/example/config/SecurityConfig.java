package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import com.example.security.CustomAuthProvider;
import com.example.security.CustomLogoutSuccessHandler;
import com.example.service.AuthService;

import jakarta.servlet.http.Cookie;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthService authService;
    private final CustomAuthProvider customAuthProvider;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    public SecurityConfig(AuthService authService, CustomAuthProvider customAuthProvider, CustomLogoutSuccessHandler customLogoutSuccessHandler) {
        this.authService = authService;
        this.customAuthProvider = customAuthProvider;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/styles.css", "/script.js", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    String username = authentication.getName();
                    String ticket = authService.generateTicket(username);
                    Cookie ssoCookie = new Cookie("ssoTicket", ticket);
                    ssoCookie.setPath("/");
                    ssoCookie.setMaxAge(60 * 60);
                    response.addCookie(ssoCookie);
                    response.sendRedirect("/home");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .deleteCookies("ssoTicket")
                .invalidateHttpSession(true)
                .permitAll()
            )
            .csrf(csrf -> csrf
                .disable()
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            )
            .addFilterBefore(new SsoTicketFilter(authService), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(customAuthProvider) // Use your custom provider
            .build();
    }

    // @Bean
    // public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
    //     return new ActiveDirectoryLdapAuthenticationProvider(
    //         "your.domain.com",
    //         "ldap://your-ad-server:389"
    //     );
    // }
}