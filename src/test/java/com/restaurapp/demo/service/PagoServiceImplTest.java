package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Pago;
import com.restaurapp.demo.domain.PagoEstado;
import com.restaurapp.demo.domain.Pedido;
import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.dto.PagoCreateDto;
import com.restaurapp.demo.repository.PagoRepository;
import com.restaurapp.demo.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PagoServiceImpl service;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(7L);
        pedido.setEstado(PedidoEstado.ABIERTO);
        pedido.setTotal(new BigDecimal("120.00"));
    }

    @Test
    void crearLanzaSiMontoExcedeSaldo() {
        when(pedidoRepository.findById(7L)).thenReturn(Optional.of(pedido), Optional.of(pedido));
        when(pagoRepository.sumByPedidoAndEstado(7L, PagoEstado.APLICADO)).thenReturn(BigDecimal.ZERO);

        PagoCreateDto dto = new PagoCreateDto(new BigDecimal("150.00"), "efectivo");

        assertThatThrownBy(() -> service.crear(7L, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Monto excede el saldo");

        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void crearPersistePagoCuandoMontoValido() {
        when(pedidoRepository.findById(7L)).thenReturn(Optional.of(pedido), Optional.of(pedido));
        when(pagoRepository.sumByPedidoAndEstado(7L, PagoEstado.APLICADO)).thenReturn(new BigDecimal("20.00"));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago saved = invocation.getArgument(0);
            saved.setId(55L);
            return saved;
        });

        PagoCreateDto dto = new PagoCreateDto(new BigDecimal("50.00"), "tarjeta");

        Long pagoId = service.crear(7L, dto);

        verify(pagoRepository).save(any(Pago.class));
        verify(pagoRepository).sumByPedidoAndEstado(7L, PagoEstado.APLICADO);
        assertThat(pagoId).isEqualTo(55L);
    }
}
