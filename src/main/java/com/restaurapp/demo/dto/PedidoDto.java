package com.restaurapp.demo.dto;
import com.restaurapp.demo.dto.PedidoItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoDto(
        Long id,
        Long mesa_id,
        String mesa_numero,
        UUID mesero_id,
        String estado,
        BigDecimal total,
        String notas,
        LocalDateTime created_at,
        LocalDateTime updated_at,
        List<PedidoItemDto> items
) {}
