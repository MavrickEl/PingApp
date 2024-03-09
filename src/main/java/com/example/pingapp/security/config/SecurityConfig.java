package com.example.pingapp.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(GET, "/api/rule").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(POST, "/api/rule").hasRole("ADMIN")
                        .requestMatchers(DELETE, "/api/rule/**").hasRole("ADMIN")
                        .requestMatchers(PATCH, "/api/rule/**").hasRole("ADMIN")
                        .requestMatchers(GET, "/api/channel").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(POST, "/api/channel").hasRole("ADMIN")
                        .requestMatchers(DELETE, "/api/channel/**").hasRole("ADMIN")
                        .requestMatchers(PATCH, "/api/channel/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .defaultSuccessUrl("/swagger-ui/index.html#")
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin"))
                .roles("ADMIN", "USER")
                .build();

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("user"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
