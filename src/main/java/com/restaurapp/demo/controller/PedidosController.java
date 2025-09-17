package com.restaurapp.demo.controller;

import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.dto.PedidoCreateDto;
import com.restaurapp.demo.dto.PedidoListDto;
import com.restaurapp.demo.dto.PedidoPatchDto;
import com.restaurapp.demo.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidosController {

    private final PedidoService service;

    // GET /pedidos?mesa_id=&estado=&desde=&hasta=&page=&size=&sort=
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> listar(
            @RequestParam(required = false, name = "mesa_id") Long mesaId,
            @RequestParam(required = false, name = "estado") String estadoStr,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        PedidoEstado estado = null;
        if (estadoStr != null && !estadoStr.isBlank()) {
            estado = PedidoEstado.valueOf(estadoStr.toUpperCase());
        }
        LocalDateTime dDesde = (desde == null || desde.isBlank()) ? null : LocalDateTime.parse(desde);
        LocalDateTime dHasta = (hasta == null || hasta.isBlank()) ? null : LocalDateTime.parse(hasta);

        Page<PedidoListDto> result = service.listar(mesaId, estado, dDesde, dHasta, page, size, sort);
        return Map.of("success", true, "data", result.getContent());
    }

    // POST /pedidos
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public Map<String, Object> crear(@Valid @RequestBody PedidoCreateDto body) {
        Long id = service.crear(body);
        return Map.of("success", true, "data", Map.of("id", id), "message", "Pedido creado.");
    }

    // GET /pedidos/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> detalle(@PathVariable Long id) {
        return Map.of("success", true, "data", service.detalle(id));
    }

    // PATCH /pedidos/{id}
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public Map<String, Object> patch(@PathVariable Long id, @RequestBody PedidoPatchDto body) {
        service.patch(id, body);
        return Map.of("success", true, "message", "Pedido actualizado.");
    }

    // POST /pedidos/{id}/enviar-a-cocina
    @PostMapping("/{id}/enviar-a-cocina")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public Map<String, Object> enviarACocina(@PathVariable Long id) {
        service.enviarACocina(id);
        return Map.of("success", true, "message", "Pedido enviado a cocina.");
    }
    // POST /pedidos/{id}/cancelar
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','MESERO')")
    public Map<String, Object> cancelar(@PathVariable Long id) {
        service.cancelar(id);
        return Map.of("success", true, "message", "Pedido cancelado.");
    }
}

