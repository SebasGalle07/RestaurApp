package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Factura;
import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.dto.FacturaDto;
import com.restaurapp.demo.dto.FacturaListDto;
import com.restaurapp.demo.repository.FacturaRepository;
import com.restaurapp.demo.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepo;
    private final PedidoRepository pedidoRepo;
    private final PagoService pagoService;

    private FacturaDto toDto(Factura f) {
        var p = f.getPedido();
        return new FacturaDto(
                f.getId(),
                f.getNumero(),
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),            // UUID en DTO si corresponde
                f.getTotal(),
                f.getFechaEmision()
        );
    }

    private FacturaListDto toListDto(Factura f) {
        var p = f.getPedido();
        return new FacturaListDto(
                f.getId(),
                f.getNumero(),
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),            // UUID en DTO si corresponde
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

        return facturaRepo.buscar(mesaId, meseroId, desde, hasta, pageable)
                .map(this::toListDto);
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
}
