package com.restaurapp.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoDto(
        Long id,
        BigDecimal monto,
        String metodo,
        String estado,
        LocalDateTime created_at
) {}
