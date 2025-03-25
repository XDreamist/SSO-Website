package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll() // Allow login page
                .anyRequest().authenticated()          // Require auth for all other requests
            )
            .formLogin(form -> form
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/welcome", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
            )
            .csrf().disable(); // Disable CSRF for simplicity
        return http.build();
    }

    @Bean
    public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider = 
            new ActiveDirectoryLdapAuthenticationProvider(
                "your.domain.com", // e.g., "example.com"
                "ldap://your-ad-server:389" // AD server URL
            );
        provider.setSearchFilter("(&(objectClass=user)(sAMAccountName={0}))");
        return provider;
    }
}