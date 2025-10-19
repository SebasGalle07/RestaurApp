package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.FacturaListDto;
import com.restaurapp.demo.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FacturasController {

    private final FacturaService service;

    // POST /pedidos/{id}/factura -> emitir
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
            @RequestParam(required = false, name = "mesero_id") UUID meseroId, // <-- CORRECCION: UUID
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "fechaEmision,desc") String sort
    ) {
        var dDesde = parseDateOrDateTime(desde, true);
        var dHasta = parseDateOrDateTime(hasta, false);
        Page<FacturaListDto> result = service.listar(mesaId, meseroId, dDesde, dHasta, page, size, sort);
        return Map.of("success", true, "data", result.getContent());
    }

    // GET /facturas/{id}
    @GetMapping("/facturas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    public Map<String, Object> detalle(@PathVariable Long id) {
        return Map.of("success", true, "data", service.detalle(id));
    }

    private LocalDateTime parseDateOrDateTime(String value, boolean startOfDay) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(value.trim());
            return startOfDay ? date.atStartOfDay() : date.atTime(23, 59, 59);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(value.trim());
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Formato de fecha inválido: " + value);
            }
        }
    }
}
