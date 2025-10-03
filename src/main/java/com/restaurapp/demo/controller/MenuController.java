package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.MenuCreateDto;
import com.restaurapp.demo.dto.MenuDto;
import com.restaurapp.demo.dto.MenuPatchDto;
import com.restaurapp.demo.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService service;

    // GET /menu?categoria_id=&activo=&q=&page=&size=&sort=
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> listar(
            @RequestParam(required = false) Long categoria_id,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        Page<MenuDto> result = service.listar(categoria_id, activo, q, page, size, sort);
        // Si quieres, agrega X-Total-Count en headers; por ahora devolvemos content en data:
        return Map.of("success", true, "data", result.getContent());
    }

    // POST /menu
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> crear(@Valid @RequestBody MenuCreateDto body) {
        Long id = service.crear(body);
        return Map.of("success", true, "data", Map.of("id", id), "message", "Item creado.");
    }

    // GET /menu/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> detalle(@PathVariable Long id) {
        return Map.of("success", true, "data", service.detalle(id));
    }

    // PATCH /menu/{id}
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> patch(@PathVariable Long id, @Valid @RequestBody MenuPatchDto body) {
        service.patch(id, body);
        return Map.of("success", true, "message", "Item actualizado.");
    }

    // DELETE /menu/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return Map.of("success", true, "message", "Item eliminado.");
    }
}
