package com.restaurapp.demo.dto;

import java.math.BigDecimal;

public record PedidoItemDto(
        Long id,
        Long item_menu_id,
        String item_nombre,
        Integer cantidad,
        BigDecimal precio_unitario,
        BigDecimal subtotal,
        String estado_preparacion,
        String notas
) {}
