package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.PagoCreateDto;
import com.restaurapp.demo.dto.PagoResultDto;
import com.restaurapp.demo.dto.PagosResponseDto;

import java.math.BigDecimal;

public interface PagoService {
    PagosResponseDto listar(Long pedidoId);
    PagoResultDto crear(Long pedidoId, PagoCreateDto dto);
    void anular(Long pedidoId, Long pagoId);

    // Utilitario para otros modulos (opcional)
    BigDecimal calcularSaldoPendiente(Long pedidoId);
}
