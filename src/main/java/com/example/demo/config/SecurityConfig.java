package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // allow static resources
                        .requestMatchers("/**.html", "/css/**", "/js/**", "/images/**").permitAll()

                        // allow auth APIs
                        .requestMatchers("/api/auth/**").permitAll()

                        // allow other APIs
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/services/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()
                        .requestMatchers("/api/hotels/**", "/api/rooms/**").permitAll()
                        .requestMatchers("/api/feedbacks/**").permitAll()

                        // require authentication for reservations

                        .requestMatchers("/api/reservations/**").permitAll()


                        // everything else requires authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable());



        return http.build();
    }
}
