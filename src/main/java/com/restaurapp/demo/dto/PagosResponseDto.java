package com.restaurapp.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public record PagosResponseDto(
        List<PagoDto> pagos,
        BigDecimal saldo_pendiente
) {}
