package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.dto.PedidoCreateDto;
import com.restaurapp.demo.dto.PedidoDto;
import com.restaurapp.demo.dto.PedidoListDto;
import com.restaurapp.demo.dto.PedidoPatchDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface PedidoService {
    Page<PedidoListDto> listar(Long mesaId, PedidoEstado estado, LocalDateTime desde, LocalDateTime hasta,
                               int page, int size, String sort);
    Long crear(PedidoCreateDto dto);
    PedidoDto detalle(Long id);
    void patch(Long id, PedidoPatchDto dto);
    void enviarACocina(Long id);
    void marcarListo(Long id);
    void marcarEntregado(Long id);
    void cancelar(Long id);
}
