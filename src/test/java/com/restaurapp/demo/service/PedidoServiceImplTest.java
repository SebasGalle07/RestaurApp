package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Mesa;
import com.restaurapp.demo.domain.Pedido;
import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.repository.MenuRepository;
import com.restaurapp.demo.repository.MesaRepository;
import com.restaurapp.demo.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PedidoServiceImpl service;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(15L);
        pedido.setEstado(PedidoEstado.ABIERTO);
        pedido.setTotal(new BigDecimal("100.00"));
        pedido.setMesa(Mesa.builder().id(3L).numero("A1").build());
        pedido.setCreatedAt(LocalDateTime.now());
        pedido.setMeseroId(UUID.randomUUID());
    }

    @Test
    void cancelarLanzaCuandoHayPagosRegistrados() {
        when(pedidoRepository.findById(15L)).thenReturn(Optional.of(pedido));
        when(pagoService.calcularSaldoPendiente(15L)).thenReturn(new BigDecimal("60.00"));

        assertThatThrownBy(() -> service.cancelar(15L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pagos registrados");
    }

    @Test
    void cancelarCambiaEstadoCuandoNoHayPagos() {
        when(pedidoRepository.findById(15L)).thenReturn(Optional.of(pedido));
        when(pagoService.calcularSaldoPendiente(15L)).thenReturn(new BigDecimal("100.00"));

        service.cancelar(15L);

        assertThat(pedido.getEstado()).isEqualTo(PedidoEstado.CANCELADO);
        verify(pedidoRepository).findById(15L);
    }
}
