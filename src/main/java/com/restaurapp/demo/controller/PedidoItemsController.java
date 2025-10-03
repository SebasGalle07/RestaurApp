package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.PedidoItemCreateDto;
import com.restaurapp.demo.dto.PedidoItemPatchDto;
import com.restaurapp.demo.dto.ItemEstadoPatchDto;
import com.restaurapp.demo.service.PedidoItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/pedidos/{pedidoId}/items")
@RequiredArgsConstructor
public class PedidoItemsController {

    private final PedidoItemService service;

    // GET /pedidos/{id}/items
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> listar(@PathVariable Long pedidoId) {
        return Map.of("success", true, "data", service.listar(pedidoId));
    }

    // POST /pedidos/{id}/items
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public Map<String, Object> crear(@PathVariable Long pedidoId, @Valid @RequestBody PedidoItemCreateDto body) {
        Long id = service.crear(pedidoId, body);
        return Map.of("success", true, "data", Map.of("id", id), "message", "Detalle agregado.");
    }

    // GET /pedidos/{id}/items/{detalle_id}
    @GetMapping("/{detalleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> detalle(@PathVariable Long pedidoId, @PathVariable Long detalleId) {
        return Map.of("success", true, "data", service.detalle(pedidoId, detalleId));
    }

    // PATCH /pedidos/{id}/items/{detalle_id}
    @PatchMapping("/{detalleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public Map<String, Object> patch(@PathVariable Long pedidoId, @PathVariable Long detalleId,
                                     @Valid @RequestBody PedidoItemPatchDto body) {
        service.patch(pedidoId, detalleId, body);
        return Map.of("success", true, "message", "Detalle actualizado.");
    }

    // DELETE /pedidos/{id}/items/{detalle_id}
    @DeleteMapping("/{detalleId}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public Map<String, Object> eliminar(@PathVariable Long pedidoId, @PathVariable Long detalleId) {
        service.eliminar(pedidoId, detalleId);
        return Map.of("success", true, "message", "Detalle eliminado.");
    }

    // PATCH /pedidos/{id}/items/{detalle_id}/estado
    @PatchMapping("/{detalleId}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO')")
    public Map<String, Object> actualizarEstado(@PathVariable Long pedidoId, @PathVariable Long detalleId,
                                                @Valid @RequestBody ItemEstadoPatchDto body) {
        service.actualizarEstado(pedidoId, detalleId, body);
        return Map.of("success", true, "message", "Estado de preparacion actualizado.");
    }
}
