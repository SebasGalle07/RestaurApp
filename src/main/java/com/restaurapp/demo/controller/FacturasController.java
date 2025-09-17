package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.FacturaListDto;
import com.restaurapp.demo.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FacturasController {

    private final FacturaService service;

    // POST /pedidos/{id}/factura  → emitir
    @PostMapping("/pedidos/{pedidoId}/factura")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public Map<String, Object> emitir(@PathVariable Long pedidoId) {
        Long id = service.emitir(pedidoId);
        return Map.of("success", true, "data", Map.of("id", id), "message", "Factura emitida.");
    }

    // GET /facturas?mesa_id=&mesero_id=&desde=&hasta=&page=&size=&sort=
    @GetMapping("/facturas")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public Map<String, Object> listar(
            @RequestParam(required = false, name = "mesa_id") Long mesaId,
            @RequestParam(required = false, name = "mesero_id") Long meseroId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "fechaEmision,desc") String sort
    ) {
        var dDesde = (desde == null || desde.isBlank()) ? null : LocalDateTime.parse(desde);
        var dHasta = (hasta == null || hasta.isBlank()) ? null : LocalDateTime.parse(hasta);
        Page<FacturaListDto> result = service.listar(mesaId, meseroId, dDesde, dHasta, page, size, sort);
        return Map.of("success", true, "data", result.getContent());
    }

    // GET /facturas/{id}
    @GetMapping("/facturas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public Map<String, Object> detalle(@PathVariable Long id) {
        return Map.of("success", true, "data", service.detalle(id));
    }
}
