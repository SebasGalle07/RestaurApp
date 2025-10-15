package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.MenuCreateDto;
import com.restaurapp.demo.dto.MenuPatchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/test-data.sql")
@Transactional
class MenuServiceIntegrationTest {

    @Autowired
    private MenuService menuService;

    @Test
    void listarFiltraPorCategoriaYActivo() {
        var page = menuService.listar(1L, true, null, 0, 10, "id,asc");
        assertThat(page.getContent())
                .extracting("nombre")
                .containsExactly("Agua", "Cafe Americano");
    }

    @Test
    void crearYDetalleDevuelvenElNuevoItem() {
        var dto = new MenuCreateDto(
                "Limonada Frozen",
                "750 ml",
                new BigDecimal("8000"),
                1L,
                true
        );

        Long id = menuService.crear(dto);
        var creado = menuService.detalle(id);

        assertThat(creado.nombre()).isEqualTo("Limonada Frozen");
        assertThat(creado.categoria_nombre()).isEqualTo("Bebidas");
    }

    @Test
    void patchActualizaCamposPrincipales() {
        var patch = new MenuPatchDto("Brownie Especial", "Con helado artesanal",
                new BigDecimal("18000"), null, true);

        menuService.patch(6L, patch);
        var actualizado = menuService.detalle(6L);

        assertThat(actualizado.nombre()).isEqualTo("Brownie Especial");
        assertThat(actualizado.descripcion()).isEqualTo("Con helado artesanal");
        assertThat(actualizado.precio()).isEqualByComparingTo("18000");
    }
}
