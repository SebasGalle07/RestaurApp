package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Role;
import com.restaurapp.demo.domain.User;
import com.restaurapp.demo.dto.LoginRequest;
import com.restaurapp.demo.dto.LoginResponse;
import com.restaurapp.demo.dto.RefreshRequest;
import com.restaurapp.demo.dto.RefreshResponse;
import com.restaurapp.demo.dto.TokenPair;
import com.restaurapp.demo.repository.UserRepository;
import com.restaurapp.demo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        // autentica contra UserDetailsService + BCrypt del provider
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        // Solo usuarios activos
        User u = userRepository.findByEmailAndActivoTrue(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo"));

        // Claim "rol" en minusculas (alineado a la doc)
        String access = jwtService.generateAccessToken(
                u.getEmail(),
                Map.of("rol", u.getRol().getValue())
        );
        String refresh = jwtService.generateRefreshToken(u.getEmail());

        return LoginResponse.builder()
                .success(true)
                .data(new TokenPair(access, jwtService.getAccessTtlSec(), refresh))
                .message("OK")
                .build();
    }

    @Transactional(readOnly = true)
    public RefreshResponse refresh(RefreshRequest req) {
        // Obtiene el subject (email) del refresh token
        String email = jwtService.parse(req.getRefresh_token()).getBody().getSubject();

        User u = userRepository.findByEmailAndActivoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo"));

        String access = jwtService.generateAccessToken(
                u.getEmail(),
                Map.of("rol", u.getRol().getValue())
        );

        return RefreshResponse.builder()
                .success(true)
                .data(new TokenPair(access, jwtService.getAccessTtlSec(), null))
                .message("OK")
                .build();
    }

    // utilidad para bootstrap: crear admin si no existe
    @Transactional
    public void ensureAdmin(String email, String rawPasswordEncodedByProvider) {
        if (!userRepository.existsByEmail(email)) {
            User u = User.builder()
                    .email(email)
                    .nombre("Admin")
                    .rol(Role.ADMIN)
                    .passwordHash(rawPasswordEncodedByProvider) // <-- debe venir ya en BCrypt si la llamas manualmente
                    .activo(true)
                    .build();
            userRepository.save(u);
        }
    }
}
