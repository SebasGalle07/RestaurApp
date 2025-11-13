package com.restaurapp.demo.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoListDto(
        Long id,
        Long mesa_id,
        String mesa_numero,
        UUID mesero_id,
        String mesero_nombre,
        String estado,
        BigDecimal total,
        LocalDateTime created_at,
        BigDecimal saldo_pendiente,
        boolean listo_para_entrega,
        boolean puede_facturar
) {}
