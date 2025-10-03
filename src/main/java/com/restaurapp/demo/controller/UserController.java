package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.CreateUserDto;
import com.restaurapp.demo.dto.UpdateUserDto;
import com.restaurapp.demo.dto.UserDto;
import com.restaurapp.demo.mapper.UserMapper;
import com.restaurapp.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    // GET /users?rol=admin|mesero|cocinero|cajero&activo=true|false
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) Boolean activo
    ) {
        List<UserDto> out = service.list(rol, activo).stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(Map.of("success", true, "data", out));
    }

    // Detalle por UUID (ruta existente para compatibilidad)
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable UUID id) {
        return ResponseEntity.ok(
                Map.of("success", true, "data", mapper.toDto(service.get(id)))
        );
    }

    // NUEVO: Detalle por codigo numerico (mas amigable)
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Map<String, Object>> detailByCodigo(@PathVariable Long codigo) {
        var user = service.getByCodigo(codigo); // metodo simple en service que delega a repo.findByCodigo(...)
        return ResponseEntity.ok(
                Map.of("success", true, "data", mapper.toDto(user))
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateUserDto dto) {
        var created = service.create(dto);
        var body = Map.of(
                "success", true,
                "message", "Usuario creado.",
                // devolvemos tambien el codigo numerico para que el front lo pueda usar
                "data", Map.of("id", created.getId(), "codigo", created.getCodigo())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @Valid @RequestBody UpdateUserDto dto) {
        service.update(id, dto);
        return ResponseEntity.ok(Map.of("success", true, "message", "Usuario actualizado."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Usuario eliminado."));
    }
}
