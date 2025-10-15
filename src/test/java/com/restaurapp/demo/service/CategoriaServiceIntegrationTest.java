package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.CategoriaCreateDto;
import com.restaurapp.demo.dto.CategoriaUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/test-data.sql")
@Transactional
class CategoriaServiceIntegrationTest {

    @Autowired
    private CategoriaService categoriaService;

    @Test
    void listarDevuelveTodasLasCategorias() {
        assertThat(categoriaService.listar())
                .hasSizeGreaterThanOrEqualTo(5)
                .extracting("nombre")
                .contains("Bebidas", "Entradas");
    }

    @Test
    void crearCategoriaDuplicadaLanzaExcepcion() {
        var dto = new CategoriaCreateDto("Bebidas", "Duplicado");
        assertThatThrownBy(() -> categoriaService.crear(dto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("ya existe");
    }

    @Test
    void actualizarCategoriaModificaDatos() {
        var dto = new CategoriaUpdateDto("Brunch", "Opciones matutinas");
        categoriaService.actualizar(5L, dto);

        var actualizada = categoriaService.detalle(5L);
        assertThat(actualizada.nombre()).isEqualTo("Brunch");
        assertThat(actualizada.descripcion()).isEqualTo("Opciones matutinas");
    }
}
