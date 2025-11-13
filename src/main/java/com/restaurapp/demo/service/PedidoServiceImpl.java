package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.*;
import com.restaurapp.demo.dto.PedidoCreateDto;
import com.restaurapp.demo.dto.PedidoDto;
import com.restaurapp.demo.dto.PedidoItemDto;
import com.restaurapp.demo.dto.PedidoItemNewDto;
import com.restaurapp.demo.dto.PedidoListDto;
import com.restaurapp.demo.dto.PedidoPatchDto;
import com.restaurapp.demo.repository.FacturaRepository;
import com.restaurapp.demo.repository.MenuRepository;
import com.restaurapp.demo.repository.MesaRepository;
import com.restaurapp.demo.repository.PagoRepository;
import com.restaurapp.demo.repository.PedidoRepository;
import com.restaurapp.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepo;
    private final MesaRepository mesaRepo;
    private final MenuRepository menuRepo;
    private final PagoRepository pagoRepo;
    private final FacturaRepository facturaRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoListDto> listar(Long mesaId, PedidoEstado estado, LocalDateTime desde, LocalDateTime hasta,
            int page, int size, String sort) {
        String[] s = (sort == null || sort.isBlank()) ? new String[] { "id", "desc" } : sort.split(",");
        Sort.Direction dir = (s.length > 1 && "asc".equalsIgnoreCase(s[1])) ? Sort.Direction.ASC : Sort.Direction.DESC;
        var pr = PageRequest.of(page, size, Sort.by(dir, s[0]));
        Page<Pedido> resultPage = pedidoRepo.buscar(mesaId, estado, desde, hasta, pr);
        Map<UUID, String> nombres = cargarNombresMeseros(resultPage.getContent());
        return resultPage.map(p -> toListDto(p, nombres.get(p.getMeseroId())));
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
                    .orElseThrow(() -> new IllegalArgumentException("Item de menu no encontrado"));
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
    @Transactional(readOnly = true)
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
        if (dto.mesero_id() != null)
            p.setMeseroId(dto.mesero_id());
        if (dto.notas() != null)
            p.setNotas(dto.notas());
    }

    @Override
    @Transactional
    public void enviarACocina(Long id) {
        var p = pedidoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        if (p.getEstado() != PedidoEstado.ABIERTO) {
            throw new IllegalStateException("Solo se puede enviar a cocina desde estado ABIERTO");
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
            throw new IllegalStateException("El pedido ya esta finalizado y no se puede cancelar.");
        }

        // Regla 2: No se puede cancelar si ya tiene pagos aplicados.
        BigDecimal pagosAplicados = pagoRepo.sumByPedidoAndEstado(id, PagoEstado.APLICADO);
        if (pagosAplicados == null) {
            pagosAplicados = BigDecimal.ZERO;
        }
        if (pagosAplicados.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("No se puede cancelar el pedido porque ya tiene pagos registrados.");
        }

        p.setEstado(PedidoEstado.CANCELADO);
    }

    @Override
    @Transactional
    public void marcarListo(Long id) {
        var p = pedidoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        if (p.getEstado() != PedidoEstado.EN_PREPARACION) {
            throw new IllegalStateException("Solo se puede marcar como LISTO desde EN_PREPARACION.");
        }
        if (!todosItemsListos(p)) {
            throw new IllegalStateException("Aun hay items pendientes o en preparacion.");
        }
        p.setEstado(PedidoEstado.LISTO);
    }

    @Override
    @Transactional
    public void marcarEntregado(Long id) {
        var p = pedidoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        if (p.getEstado() != PedidoEstado.LISTO) {
            throw new IllegalStateException("Solo se puede marcar como ENTREGADO un pedido LISTO.");
        }
        if (!todosItemsListos(p)) {
            throw new IllegalStateException("Hay items que no estan LISTO.");
        }
        p.setEstado(PedidoEstado.ENTREGADO);
    }

    private void recalcularTotal(Pedido p) {
        BigDecimal total = p.getItems().stream()
                .map(PedidoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        p.setTotal(total);
    }

    private PedidoListDto toListDto(Pedido p, String meseroNombre) {
        BigDecimal saldo = calcularSaldoPendiente(p);
        boolean listoEntrega = p.getEstado() == PedidoEstado.LISTO;
        boolean facturable = esFacturable(p, saldo);

        return new PedidoListDto(
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),
                meseroNombre,
                p.getEstado().name(),
                p.getTotal(),
                p.getCreatedAt(),
                saldo,
                listoEntrega,
                facturable);
    }

    private PedidoDto toDto(Pedido p) {
        BigDecimal saldo = calcularSaldoPendiente(p);
        boolean listoEntrega = p.getEstado() == PedidoEstado.LISTO;
        boolean facturable = esFacturable(p, saldo);
        String meseroNombre = obtenerNombreMesero(p.getMeseroId());

        var items = p.getItems().stream().map(it -> new PedidoItemDto(
                it.getId(),
                it.getItemMenu().getId(),
                it.getItemMenu().getNombre(),
                it.getCantidad(),
                it.getPrecioUnitario(),
                it.getSubtotal(),
                it.getEstadoPreparacion().name(),
                it.getNotas())).toList();

        return new PedidoDto(
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),
                meseroNombre,
                p.getEstado().name(),
                p.getTotal(),
                p.getNotas(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                saldo,
                listoEntrega,
                facturable,
                items);
    }

    private Map<UUID, String> cargarNombresMeseros(List<Pedido> pedidos) {
        Set<UUID> ids = pedidos.stream()
                .map(Pedido::getMeseroId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<UUID, String> map = new HashMap<>();
        userRepo.findAllById(ids).forEach(u -> map.put(u.getId(), u.getNombre()));
        return map;
    }

    private String obtenerNombreMesero(UUID meseroId) {
        if (meseroId == null) {
            return null;
        }
        return userRepo.findById(meseroId)
                .map(User::getNombre)
                .orElse(null);
    }

    private BigDecimal calcularSaldoPendiente(Pedido p) {
        BigDecimal pagos = pagoRepo.sumByPedidoAndEstado(p.getId(), PagoEstado.APLICADO);
        if (pagos == null) {
            pagos = BigDecimal.ZERO;
        }
        BigDecimal saldo = p.getTotal().subtract(pagos);
        return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
    }

    private boolean todosItemsListos(Pedido p) {
        if (p.getItems() == null || p.getItems().isEmpty()) {
            return false;
        }
        return p.getItems().stream().allMatch(it -> it.getEstadoPreparacion() == ItemEstado.LISTO);
    }

    private boolean esFacturable(Pedido p, BigDecimal saldoPendiente) {
        if (p.getEstado() != PedidoEstado.ENTREGADO) {
            return false;
        }
        if (saldoPendiente.compareTo(BigDecimal.ZERO) > 0) {
            return false;
        }
        return !facturaRepo.existsByPedidoId(p.getId());
    }
}
