package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.LoginRequest;
import com.restaurapp.demo.dto.LoginResponse;
import com.restaurapp.demo.dto.RefreshRequest;
import com.restaurapp.demo.dto.RefreshResponse;
import com.restaurapp.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        // Respuesta:
        // { "success": true, "data": { "access_token": "...", "expires_in": 3600, "refresh_token": "..." } }
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        // Respuesta:
        // { "success": true, "data": { "access_token": "...", "expires_in": 3600 } }
        return ResponseEntity.ok(authService.refresh(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // En JWT stateless no hay invalidación de servidor (a menos que uses blacklist/Redis)
        return ResponseEntity.ok(Map.of("success", true, "message", "Sesion cerrada."));
    }
}
