package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.FacturaDto;
import com.restaurapp.demo.dto.FacturaListDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FacturaService {
    Long emitir(Long pedidoId);
    FacturaDto detalle(Long facturaId);

    Page<FacturaListDto> listar(Long mesaId, UUID meseroId,
                                LocalDateTime desde, LocalDateTime hasta,
                                int page, int size, String sort);
}
