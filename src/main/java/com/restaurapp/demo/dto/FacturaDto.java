// FacturaDto.java
package com.restaurapp.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FacturaDto(
        Long id,
        String numero,
        Long pedido_id,
        Long mesa_id,
        String mesa_numero,
        java.util.UUID mesero_id,
        String mesero_nombre,
        BigDecimal total,
        LocalDateTime fecha_emision
) {}
