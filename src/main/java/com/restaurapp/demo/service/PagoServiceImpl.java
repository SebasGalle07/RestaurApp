package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.*;
import com.restaurapp.demo.dto.PagoCreateDto;
import com.restaurapp.demo.dto.PagoDto;
import com.restaurapp.demo.dto.PagosResponseDto;
import com.restaurapp.demo.repository.PagoRepository;
import com.restaurapp.demo.repository.PedidoRepository;
import com.restaurapp.demo.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepo;
    private final PedidoRepository pedidoRepo;

    private PagoDto toDto(Pago p) {
        return new PagoDto(
                p.getId(),
                p.getMonto(),
                p.getMetodo().name(),
                p.getEstado().name(),
                p.getCreatedAt()
        );
    }

    @Override
    public PagosResponseDto listar(Long pedidoId) {
        var pagos = pagoRepo.findByPedidoId(pedidoId).stream().map(this::toDto).toList();
        var saldo = calcularSaldoPendiente(pedidoId);
        return new PagosResponseDto(pagos, saldo);
    }

    @Override
    @Transactional
    public Long crear(Long pedidoId, PagoCreateDto dto) {
        var pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        if (pedido.getEstado() == PedidoEstado.CANCELADO || pedido.getEstado() == PedidoEstado.CERRADO) {
            throw new IllegalStateException("No se pueden registrar pagos para un pedido " + pedido.getEstado());
        }

        // Validar método
        PagoMetodo metodo;
        try { metodo = PagoMetodo.valueOf(dto.metodo().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("Método de pago inválido"); }

        // Validar que no exceda saldo
        BigDecimal saldo = calcularSaldoPendiente(pedidoId);
        if (dto.monto().compareTo(saldo) > 0) {
            throw new IllegalStateException("Monto excede el saldo pendiente (" + saldo + ")");
        }

        var pago = Pago.builder()
                .pedido(pedido)
                .monto(dto.monto())
                .metodo(metodo)
                .estado(PagoEstado.APLICADO)
                .build();

        pagoRepo.save(pago);
        return pago.getId();
    }

    @Override
    @Transactional
    public void anular(Long pedidoId, Long pagoId) {
        var pago = pagoRepo.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));
        if (!pago.getPedido().getId().equals(pedidoId)) {
            throw new IllegalArgumentException("El pago no pertenece al pedido");
        }
        if (pago.getEstado() == PagoEstado.ANULADO) return; // idempotente
        pago.setEstado(PagoEstado.ANULADO);
    }

    @Override
    public BigDecimal calcularSaldoPendiente(Long pedidoId) {
        var pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        BigDecimal totalPagos = pagoRepo.sumByPedidoAndEstado(pedidoId, PagoEstado.APLICADO);
        if (totalPagos == null) totalPagos = BigDecimal.ZERO;
        return pedido.getTotal().subtract(totalPagos).max(BigDecimal.ZERO);
    }
}
