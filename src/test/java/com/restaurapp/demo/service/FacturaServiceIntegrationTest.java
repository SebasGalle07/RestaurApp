package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.PedidoEstado;
import com.restaurapp.demo.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/test-data.sql")
@Transactional
class FacturaServiceIntegrationTest {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    void listarPorMeseroFiltraResultados() {
        var meseroId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        var page = facturaService.listar(null, meseroId, null, null, 0, 10, "fechaEmision,desc");
        assertThat(page.getContent())
                .isNotEmpty()
                .allMatch(f -> meseroId.equals(f.mesero_id()));
    }

    @Test
    void emitirGeneraFacturaYNumeracionCorrelativa() {
        Long facturaId = facturaService.emitir(8L);

        var factura = facturaService.detalle(facturaId);
        assertThat(factura.numero()).isEqualTo("F-00000006");
        assertThat(factura.total()).isEqualByComparingTo("30000");

        var pedido = pedidoRepository.findById(8L).orElseThrow();
        assertThat(pedido.getEstado()).isEqualTo(PedidoEstado.CERRADO);

        assertThatThrownBy(() -> facturaService.emitir(8L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("facturado");
    }
}
