package com.sivi.wallet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho REST API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/auth/**").permitAll() // Mở khóa API Health & Auth
                        .anyRequest().authenticated() // Các API còn lại bắt buộc đăng nhập
                );
        return http.build();
    }
}