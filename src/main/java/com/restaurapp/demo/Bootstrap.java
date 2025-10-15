package com.restaurapp.demo;

import com.restaurapp.demo.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@org.springframework.context.annotation.Profile("!test")
public class Bootstrap {
    @Bean
    CommandLineRunner initAdmin(AuthService authService, PasswordEncoder encoder) {
        return args -> authService.ensureAdmin("admin@resto.com", encoder.encode("Secreta123"));
    }
}
