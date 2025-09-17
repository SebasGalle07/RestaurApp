package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.*;
import com.restaurapp.demo.dto.PedidoCreateDto;
import com.restaurapp.demo.dto.PedidoDto;
import com.restaurapp.demo.dto.PedidoItemDto;
import com.restaurapp.demo.dto.PedidoItemNewDto;
import com.restaurapp.demo.dto.PedidoListDto;
import com.restaurapp.demo.dto.PedidoPatchDto;
import com.restaurapp.demo.repository.MenuRepository;
import com.restaurapp.demo.repository.MesaRepository;
import com.restaurapp.demo.repository.PedidoRepository;
import com.restaurapp.demo.service.PagoService;
import com.restaurapp.demo.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepo;
    private final MesaRepository mesaRepo;
    private final MenuRepository menuRepo;
    private final PagoService pagoService;

    @Override
    public Page<PedidoListDto> listar(Long mesaId, PedidoEstado estado, LocalDateTime desde, LocalDateTime hasta,
                                      int page, int size, String sort) {
        String[] s = (sort == null || sort.isBlank()) ? new String[]{"id","desc"} : sort.split(",");
        Sort.Direction dir = (s.length > 1 && "asc".equalsIgnoreCase(s[1])) ? Sort.Direction.ASC : Sort.Direction.DESC;
        var pr = PageRequest.of(page, size, Sort.by(dir, s[0]));
        return pedidoRepo.buscar(mesaId, estado, desde, hasta, pr).map(this::toListDto);
    }

    @Override
    @Transactional
    public Long crear(PedidoCreateDto dto) {
        var mesa = mesaRepo.findById(dto.mesa_id())
                .orElseThrow(() -> new IllegalArgumentException("Mesa no encontrada"));

        Pedido p = new Pedido();
        p.setMesa(mesa);
        p.setMeseroId(dto.mesero_id());
        p.setEstado(PedidoEstado.ABIERTO);
        p.setNotas(dto.notas());
        p.setTotal(BigDecimal.ZERO);
        p.setItems(new ArrayList<>());

        // agregar items
        for (PedidoItemNewDto it : dto.items()) {
            var menu = menuRepo.findById(it.item_menu_id())
                    .orElseThrow(() -> new IllegalArgumentException("Ítem de menú no encontrado"));
            var item = new PedidoItem();
            item.setPedido(p);
            item.setItemMenu(menu);
            item.setCantidad(it.cantidad());
            item.setPrecioUnitario(menu.getPrecio());
            item.setSubtotal(menu.getPrecio().multiply(BigDecimal.valueOf(it.cantidad())));
            item.setEstadoPreparacion(ItemEstado.PENDIENTE);
            item.setNotas(it.notas());
            p.getItems().add(item);
        }
        recalcularTotal(p);
        pedidoRepo.save(p);
        return p.getId();
    }

    @Override
    public PedidoDto detalle(Long id) {
        var p = pedidoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        return toDto(p);
    }

    @Override
    @Transactional
    public void patch(Long id, PedidoPatchDto dto) {
        var p = pedidoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        if (p.getEstado() == PedidoEstado.CERRADO || p.getEstado() == PedidoEstado.CANCELADO) {
            throw new IllegalStateException("Pedido no editable en estado " + p.getEstado());
        }
        if (dto.mesero_id() != null) p.setMeseroId(dto.mesero_id());
        if (dto.notas() != null) p.setNotas(dto.notas());
    }

    @Override
    @Transactional
    public void enviarACocina(Long id) {
        var p = pedidoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        if (p.getEstado() != PedidoEstado.ABIERTO) {
            throw new IllegalStateException("Solo se puede enviar a cocina desde estado ABIERTPO");
        }
        p.setEstado(PedidoEstado.EN_PREPARACION);
        p.getItems().forEach(it -> {
            if (it.getEstadoPreparacion() == ItemEstado.PENDIENTE) {
                it.setEstadoPreparacion(ItemEstado.EN_PREPARACION);
            }
        });
    }

    @Override
    @Transactional
    public void cancelar(Long id) {
        var p = pedidoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        // Regla 1: No se puede cancelar un pedido ya cerrado o cancelado.
        if (p.getEstado() == PedidoEstado.CERRADO || p.getEstado() == PedidoEstado.CANCELADO) {
            throw new IllegalStateException("El pedido ya está finalizado y no se puede cancelar.");
        }

        // Regla 2: No se puede cancelar si ya tiene pagos aplicados.
        BigDecimal saldoPagado = p.getTotal().subtract(pagoService.calcularSaldoPendiente(id));
        if (saldoPagado.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("No se puede cancelar el pedido porque ya tiene pagos registrados.");
        }

        p.setEstado(PedidoEstado.CANCELADO);
    }

    private void recalcularTotal(Pedido p) {
        BigDecimal total = p.getItems().stream()
                .map(PedidoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        p.setTotal(total);
    }

    private PedidoListDto toListDto(Pedido p) {
        return new PedidoListDto(
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),
                p.getEstado().name(),
                p.getTotal(),
                p.getCreatedAt()
        );
    }

    private PedidoDto toDto(Pedido p) {
        var items = p.getItems().stream().map(it ->
                new PedidoItemDto(
                        it.getId(),
                        it.getItemMenu().getId(),
                        it.getItemMenu().getNombre(),
                        it.getCantidad(),
                        it.getPrecioUnitario(),
                        it.getSubtotal(),
                        it.getEstadoPreparacion().name(),
                        it.getNotas()
                )
        ).toList();

        return new PedidoDto(
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),
                p.getEstado().name(),
                p.getTotal(),
                p.getNotas(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                items
        );
    }
}