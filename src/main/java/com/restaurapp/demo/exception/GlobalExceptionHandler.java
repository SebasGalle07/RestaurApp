package com.restaurapp.demo.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Para errores de validacion de datos (ej. ID no encontrado, estado invalido)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = Map.of("success", false, "message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body); // 400 Bad Request
    }

    // Para violaciones de reglas de negocio (ej. no se puede facturar pedido con saldo)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> body = Map.of("success", false, "message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body); // 409 Conflict
    }

    // Para violaciones de constraints de la BD (ej. email duplicado, item en uso)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Conflicto de datos. Es posible que el recurso ya exista o este en uso.";
        if (ex.getMessage() != null && ex.getMessage().contains("ya existe")) {
            message = ex.getMessage();
        }
        Map<String, Object> body = Map.of("success", false, "message", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body); // 409 Conflict
    }

    // Para errores de autenticacion -> 401 Unauthorized
    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class,
            InternalAuthenticationServiceException.class,
            AccountStatusException.class
    })
    public ResponseEntity<Map<String, Object>> handleAuthErrors(Exception ex) {
        Map<String, Object> body = Map.of("success", false, "message", "Credenciales invalidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // Refresh token invalido/expirado
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(JwtException ex) {
        Map<String, Object> body = Map.of("success", false, "message", "Token invalido o expirado");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // Un "catch-all" para cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = Map.of("success", false, "message", "Ocurrio un error interno en el servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body); // 500 Internal Server Error
    }
}
