package com.restaurapp.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Para errores de validación de datos (ej. ID no encontrado, estado inválido)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Asignamos el mapa a una variable del tipo explícito Map<String, Object>
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
        String message = "Conflicto de datos. Es posible que el recurso ya exista o esté en uso.";
        // Mantenemos la lógica para mensajes personalizados
        if (ex.getMessage() != null && ex.getMessage().contains("ya existe")) {
            message = ex.getMessage();
        }
        Map<String, Object> body = Map.of("success", false, "message", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body); // 409 Conflict
    }

    // Un "catch-all" para cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // log.error("Error inesperado: ", ex); // Buena práctica: registrar el error
        Map<String, Object> body = Map.of("success", false, "message", "Ocurrió un error interno en el servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body); // 500 Internal Server Error
    }
}