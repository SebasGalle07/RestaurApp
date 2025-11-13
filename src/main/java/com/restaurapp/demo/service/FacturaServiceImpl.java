package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Factura;
import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.dto.FacturaDto;
import com.restaurapp.demo.dto.FacturaListDto;
import com.restaurapp.demo.repository.FacturaRepository;
import com.restaurapp.demo.repository.PedidoRepository;
import com.restaurapp.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepo;
    private final PedidoRepository pedidoRepo;
    private final PagoService pagoService;
    private final UserRepository userRepo;

    private FacturaDto toDto(Factura f, String meseroNombre) {
        var p = f.getPedido();
        return new FacturaDto(
                f.getId(),
                f.getNumero(),
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),
                meseroNombre,
                f.getTotal(),
                f.getFechaEmision()
        );
    }

    private FacturaDto toDto(Factura f) {
        return toDto(f, obtenerNombreMesero(f.getPedido().getMeseroId()));
    }

    private FacturaListDto toListDto(Factura f, String meseroNombre) {
        var p = f.getPedido();
        return new FacturaListDto(
                f.getId(),
                f.getNumero(),
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),
                meseroNombre,
                f.getTotal(),
                f.getFechaEmision()
        );
    }

    @Override
    @Transactional
    public Long emitir(Long pedidoId) {
        var pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (pedido.getEstado() == PedidoEstado.CANCELADO) {
            throw new IllegalStateException("No se puede facturar un pedido CANCELADO");
        }
        if (facturaRepo.existsByPedidoId(pedidoId)) {
            throw new IllegalStateException("El pedido ya fue facturado");
        }

        BigDecimal saldo = pagoService.calcularSaldoPendiente(pedidoId);
        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("No se puede facturar: saldo pendiente " + saldo);
        }

        var f = new Factura();
        f.setPedido(pedido);
        f.setTotal(pedido.getTotal());
        f.setNumero("PEND");
        facturaRepo.saveAndFlush(f);

        f.setNumero("F-" + String.format("%08d", f.getId()));
        pedido.setEstado(PedidoEstado.CERRADO);
        return f.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaDto detalle(Long facturaId) {
        var f = facturaRepo.findById(facturaId)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
        return toDto(f);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FacturaListDto> listar(Long mesaId,
                                       UUID meseroId,     // UUID para coincidir con repo y dominio
                                       LocalDateTime desde,
                                       LocalDateTime hasta,
                                       int page,
                                       int size,
                                       String sort) {
        int p = Math.max(page, 0);
        int s = Math.min(Math.max(size, 1), 200);

        Sort sortObj = buildSort(sort); // tu helper de sort
        Pageable pageable = PageRequest.of(p, s, sortObj);

        Page<Factura> result = facturaRepo.buscar(mesaId, meseroId, desde, hasta, pageable);
        Map<UUID, String> nombres = cargarNombresMeseros(result.getContent());
        return result.map(f -> toListDto(f, nombres.get(f.getPedido().getMeseroId())));
    }


    private Sort buildSort(String sort) {
        String defaultField = "fechaEmision";
        Sort.Direction defaultDir = Sort.Direction.DESC;

        if (sort == null || sort.isBlank()) return Sort.by(defaultDir, defaultField);

        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();
        String dir = (parts.length > 1 ? parts[1].trim().toLowerCase() : "desc");

        Sort.Direction direction = ("asc".equals(dir)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field.isEmpty() ? defaultField : field);
    }
    private Map<UUID, String> cargarNombresMeseros(List<Factura> facturas) {
        Set<UUID> ids = facturas.stream()
                .map(f -> f.getPedido().getMeseroId())
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
                .map(com.restaurapp.demo.domain.User::getNombre)
                .orElse(null);
    }
}
