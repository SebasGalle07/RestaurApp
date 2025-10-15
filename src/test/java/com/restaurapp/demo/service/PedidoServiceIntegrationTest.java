package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/test-data.sql")
@Transactional
class PedidoServiceIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void listarFiltraPorEstadoEspecifico() {
        var page = pedidoService.listar(null, PedidoEstado.CERRADO, null, null, 0, 10, "id,asc");
        assertThat(page.getContent())
                .hasSize(5)
                .allMatch(dto -> dto.estado().equals("CERRADO"));
    }

    @Test
    void cancelarPedidoAbiertoSinPagosCambiaEstado() {
        pedidoService.cancelar(7L);
        var pedido = pedidoRepository.findById(7L).orElseThrow();
        assertThat(pedido.getEstado()).isEqualTo(PedidoEstado.CANCELADO);
    }

    @Test
    void cancelarPedidoConPagosLanzaExcepcion() {
        assertThatThrownBy(() -> pedidoService.cancelar(6L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pagos registrados");
    }
}
