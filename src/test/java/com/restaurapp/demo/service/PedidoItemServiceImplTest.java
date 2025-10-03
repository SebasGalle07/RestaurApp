package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.MenuItem;
import com.restaurapp.demo.domain.Pedido;
import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.domain.PedidoItem;
import com.restaurapp.demo.dto.PedidoItemCreateDto;
import com.restaurapp.demo.repository.MenuRepository;
import com.restaurapp.demo.repository.PedidoItemRepository;
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
class PedidoItemServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoItemRepository itemRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private PedidoItemServiceImpl service;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(PedidoEstado.ABIERTO);
        pedido.setItems(new java.util.ArrayList<>());
        pedido.setTotal(BigDecimal.ZERO);
    }

    @Test
    void crearGuardaItemYRecalculaTotal() {
        MenuItem menuItem = MenuItem.builder()
                .id(10L)
                .nombre("Hamburguesa")
                .precio(new BigDecimal("8.50"))
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(menuRepository.findById(10L)).thenReturn(Optional.of(menuItem));
        when(itemRepository.save(any(PedidoItem.class))).thenAnswer(invocation -> {
            PedidoItem saved = invocation.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        PedidoItemCreateDto dto = new PedidoItemCreateDto(10L, 2, "sin cebolla");

        Long id = service.crear(1L, dto);

        assertThat(id).isEqualTo(200L);
        assertThat(pedido.getItems()).hasSize(1);
        assertThat(pedido.getTotal()).isEqualByComparingTo(new BigDecimal("17.00"));
        verify(itemRepository).save(any(PedidoItem.class));
    }

    @Test
    void crearLanzaExcepcionCuandoPedidoNoEsEditable() {
        pedido.setEstado(PedidoEstado.CERRADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoItemCreateDto dto = new PedidoItemCreateDto(10L, 1, null);

        assertThatThrownBy(() -> service.crear(1L, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Pedido no editable");

        verify(menuRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }
}
