package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.*;
import com.restaurapp.demo.dto.FacturaDto;
import com.restaurapp.demo.dto.FacturaListDto;
import com.restaurapp.demo.repository.FacturaRepository;
import com.restaurapp.demo.repository.PedidoRepository;
import com.restaurapp.demo.service.FacturaService;
import com.restaurapp.demo.service.PagoService;
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
    private final PagoService pagoService; // usamos para calcular saldo pendiente

    private FacturaDto toDto(Factura f) {
        var p = f.getPedido();
        return new FacturaDto(
                f.getId(),
                f.getNumero(),
                p.getId(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMeseroId(),
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
                p.getMeseroId(),
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

        // Crear factura con total del pedido; el numero lo generamos luego usando el id
        Factura f = new Factura();
        f.setPedido(pedido);
        f.setTotal(pedido.getTotal());
        f.setNumero("PEND"); // placeholder
        facturaRepo.saveAndFlush(f); // aseguramos id

        //  Generar numero basado en id (único y transaccional)
        String numero = "F-" + String.format("%08d", f.getId());
        f.setNumero(numero);

        // Marcar pedido como CERRADO
        pedido.setEstado(PedidoEstado.CERRADO);

        return f.getId();
    }

    @Override
    public FacturaDto detalle(Long facturaId) {
        var f = facturaRepo.findById(facturaId)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
        return toDto(f);
    }

    @Override
    // <-- CORRECCIÓN: Cambiado de Long a UUID para meseroId
    public Page<FacturaListDto> listar(Long mesaId, UUID meseroId, LocalDateTime desde, LocalDateTime hasta,
                                       int page, int size, String sort) {
        String[] s = (sort == null || sort.isBlank()) ? new String[]{"fechaEmision","desc"} : sort.split(",");
        Sort.Direction dir = (s.length > 1 && "asc".equalsIgnoreCase(s[1])) ? Sort.Direction.ASC : Sort.Direction.DESC;
        var pr = PageRequest.of(page, size, Sort.by(dir, s[0]));

        // Ahora la llamada al repositorio es correcta
        return facturaRepo.buscar(mesaId, meseroId, desde, hasta, pr).map(this::toListDto);
    }
}
