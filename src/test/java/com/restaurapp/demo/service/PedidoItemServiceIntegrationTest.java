package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.PedidoItemPatchDto;
import com.restaurapp.demo.repository.PedidoItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/test-data.sql")
@Transactional
class PedidoItemServiceIntegrationTest {

    @Autowired
    private PedidoItemService pedidoItemService;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Test
    void listarDevuelveItemsDelPedido() {
        var items = pedidoItemService.listar(1L);
        assertThat(items)
                .hasSize(3)
                .extracting("item_nombre")
                .contains("Ceviche Clasico", "Brownie con Helado");
    }

    @Test
    void patchActualizaCantidadYNotas() {
        var patch = new PedidoItemPatchDto(2, "Servir tibio");
        pedidoItemService.patch(7L, 14L, patch);

        var actualizado = pedidoItemRepository.findById(14L).orElseThrow();
        assertThat(actualizado.getCantidad()).isEqualTo(2);
        assertThat(actualizado.getNotas()).isEqualTo("Servir tibio");
    }
}
