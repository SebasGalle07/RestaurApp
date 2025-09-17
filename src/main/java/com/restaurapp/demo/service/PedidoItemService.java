package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.PedidoItemCreateDto;
import com.restaurapp.demo.dto.PedidoItemDto;
import com.restaurapp.demo.dto.PedidoItemPatchDto;
import com.restaurapp.demo.dto.ItemEstadoPatchDto;

import java.util.List;

public interface PedidoItemService {
    List<PedidoItemDto> listar(Long pedidoId);
    Long crear(Long pedidoId, PedidoItemCreateDto dto);
    PedidoItemDto detalle(Long pedidoId, Long detalleId);
    void patch(Long pedidoId, Long detalleId, PedidoItemPatchDto dto);
    void eliminar(Long pedidoId, Long detalleId);
    void actualizarEstado(Long pedidoId, Long detalleId, ItemEstadoPatchDto dto);
}
