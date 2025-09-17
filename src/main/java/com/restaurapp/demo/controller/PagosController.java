package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.PagoCreateDto;
import com.restaurapp.demo.dto.PagosResponseDto;
import com.restaurapp.demo.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/pedidos/{pedidoId}/pagos")
@RequiredArgsConstructor
public class PagosController {

    private final PagoService service;

    // GET /pedidos/{pedidoId}/pagos
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> listar(@PathVariable Long pedidoId) {
        PagosResponseDto data = service.listar(pedidoId);
        return Map.of("success", true, "data", data);
    }

    // POST /pedidos/{pedidoId}/pagos
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public Map<String, Object> crear(@PathVariable Long pedidoId, @Valid @RequestBody PagoCreateDto body) {
        Long id = service.crear(pedidoId, body);
        return Map.of("success", true, "data", Map.of("id", id), "message", "Pago registrado.");
    }

    // DELETE /pedidos/{pedidoId}/pagos/{pagoId}  (anular)
    @DeleteMapping("/{pagoId}")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public Map<String, Object> anular(@PathVariable Long pedidoId, @PathVariable Long pagoId) {
        service.anular(pedidoId, pagoId);
        return Map.of("success", true, "message", "Pago anulado.");
    }
}
