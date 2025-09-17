package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.FacturaDto;
import com.restaurapp.demo.dto.FacturaListDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface FacturaService {
    Long emitir(Long pedidoId); // crea factura (si saldo=0) y cierra el pedido
    FacturaDto detalle(Long facturaId);
    Page<FacturaListDto> listar(Long mesaId, Long meseroId, LocalDateTime desde, LocalDateTime hasta,
                                int page, int size, String sort);
}
