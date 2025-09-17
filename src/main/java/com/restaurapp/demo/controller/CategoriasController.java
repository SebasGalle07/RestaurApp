package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.CategoriaCreateDto;
import com.restaurapp.demo.dto.CategoriaUpdateDto;
import com.restaurapp.demo.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriasController {

    private final CategoriaService service;

    // GET /categorias
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> listar() {
        return Map.of("success", true, "data", service.listar());
    }

    // POST /categorias
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> crear(@Valid @RequestBody CategoriaCreateDto body) {
        Long id = service.crear(body);
        return Map.of("success", true, "data", Map.of("id", id), "message", "Categoría creada.");
    }

    // GET /categorias/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> detalle(@PathVariable Long id) {
        return Map.of("success", true, "data", service.detalle(id));
    }

    // PUT /categorias/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaUpdateDto body) {
        service.actualizar(id, body);
        return Map.of("success", true, "message", "Categoría actualizada.");
    }

    // DELETE /categorias/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return Map.of("success", true, "message", "Categoría eliminada.");
    }
}
