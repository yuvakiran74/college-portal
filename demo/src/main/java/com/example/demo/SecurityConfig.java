package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login.html", "/register.html", "/style.css", "/script.js", "/images/**",
                                "/student.html", "/faculty.html", "/food_admin.html", "/admin_dashboard.html",
                                "/auth/**", "/admin/**")
                        .permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}
