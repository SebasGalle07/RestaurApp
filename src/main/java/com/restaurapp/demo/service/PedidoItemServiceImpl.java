package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.*;
import com.restaurapp.demo.dto.PedidoItemCreateDto;
import com.restaurapp.demo.dto.PedidoItemDto;
import com.restaurapp.demo.dto.PedidoItemPatchDto;
import com.restaurapp.demo.dto.ItemEstadoPatchDto;
import com.restaurapp.demo.repository.*;
import com.restaurapp.demo.service.PedidoItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoItemServiceImpl implements PedidoItemService {

    private final PedidoRepository pedidoRepo;
    private final PedidoItemRepository itemRepo;
    private final MenuRepository menuRepo;

    private PedidoItemDto toDto(PedidoItem it) {
        return new PedidoItemDto(
                it.getId(),
                it.getItemMenu().getId(),
                it.getItemMenu().getNombre(),
                it.getCantidad(),
                it.getPrecioUnitario(),
                it.getSubtotal(),
                it.getEstadoPreparacion().name(),
                it.getNotas());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoItemDto> listar(Long pedidoId) {
        var list = itemRepo.findByPedidoId(pedidoId);
        return list.stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public Long crear(Long pedidoId, PedidoItemCreateDto dto) {
        var p = pedidoRepo.findById(pedidoId).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        if (p.getEstado() == PedidoEstado.CERRADO || p.getEstado() == PedidoEstado.CANCELADO) {
            throw new IllegalStateException("Pedido no editable en estado " + p.getEstado());
        }
        var menu = menuRepo.findById(dto.item_menu_id())
                .orElseThrow(() -> new IllegalArgumentException("Item de menu no encontrado"));

        var it = new PedidoItem();
        it.setPedido(p);
        it.setItemMenu(menu);
        it.setCantidad(dto.cantidad());
        it.setPrecioUnitario(menu.getPrecio());
        it.setSubtotal(menu.getPrecio().multiply(BigDecimal.valueOf(dto.cantidad())));
        it.setEstadoPreparacion(ItemEstado.PENDIENTE);
        it.setNotas(dto.notas());

        p.getItems().add(it);
        itemRepo.save(it);
        recalcularTotal(p);
        return it.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoItemDto detalle(Long pedidoId, Long detalleId) {
        var it = itemRepo.findById(detalleId).orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado"));
        if (!it.getPedido().getId().equals(pedidoId))
            throw new IllegalArgumentException("Detalle no pertenece al pedido");
        return toDto(it);
    }

    @Override
    @Transactional
    public void patch(Long pedidoId, Long detalleId, PedidoItemPatchDto dto) {
        var it = itemRepo.findById(detalleId).orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado"));
        var p = it.getPedido();
        if (!p.getId().equals(pedidoId))
            throw new IllegalArgumentException("Detalle no pertenece al pedido");
        if (p.getEstado() == PedidoEstado.CERRADO || p.getEstado() == PedidoEstado.CANCELADO) {
            throw new IllegalStateException("Pedido no editable en estado " + p.getEstado());
        }
        if (dto.cantidad() != null) {
            it.setCantidad(dto.cantidad());
            it.setSubtotal(it.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.cantidad())));
        }
        if (dto.notas() != null)
            it.setNotas(dto.notas());
        recalcularTotal(p);
    }

    @Override
    @Transactional
    public void eliminar(Long pedidoId, Long detalleId) {
        var it = itemRepo.findById(detalleId).orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado"));
        var p = it.getPedido();
        if (!p.getId().equals(pedidoId))
            throw new IllegalArgumentException("Detalle no pertenece al pedido");
        if (p.getEstado() == PedidoEstado.CERRADO || p.getEstado() == PedidoEstado.CANCELADO) {
            throw new IllegalStateException("Pedido no editable en estado " + p.getEstado());
        }
        p.getItems().remove(it);
        itemRepo.delete(it);
        recalcularTotal(p);
    }

    @Override
    @Transactional
    public void actualizarEstado(Long pedidoId, Long detalleId, ItemEstadoPatchDto dto) {
        var it = itemRepo.findById(detalleId).orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado"));
        var p = it.getPedido();
        if (!p.getId().equals(pedidoId))
            throw new IllegalArgumentException("Detalle no pertenece al pedido");

        ItemEstado nuevo;
        try {
            nuevo = ItemEstado.valueOf(dto.estado_preparacion().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado invalido");
        }

        // Validar transicion simple: PENDIENTE -> EN_PREPARACION -> LISTO
        ItemEstado actual = it.getEstadoPreparacion();
        boolean ok = (actual == ItemEstado.PENDIENTE
                && (nuevo == ItemEstado.EN_PREPARACION || nuevo == ItemEstado.PENDIENTE))
                || (actual == ItemEstado.EN_PREPARACION
                && (nuevo == ItemEstado.LISTO || nuevo == ItemEstado.EN_PREPARACION))
                || (actual == ItemEstado.LISTO && nuevo == ItemEstado.LISTO);
        if (!ok)
            throw new IllegalStateException("Transicion de estado no permitida");

        it.setEstadoPreparacion(nuevo);

        // Actualizar estado del pedido si corresponde
        if (p.getEstado() == PedidoEstado.EN_PREPARACION) {
            boolean todosListos = p.getItems().stream().allMatch(x -> x.getEstadoPreparacion() == ItemEstado.LISTO);
            if (todosListos)
                p.setEstado(PedidoEstado.LISTO);
        }
    }

    private void recalcularTotal(Pedido p) {
        var total = p.getItems().stream()
                .map(PedidoItem::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        p.setTotal(total);
    }
}
