package com.restaurapp.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/test-data.sql")
class MesaServiceIntegrationTest {

    @Autowired
    private MesaService mesaService;

    @Test
    void listarDevuelveTodasLasMesas() {
        var mesas = mesaService.listar();
        assertThat(mesas)
                .hasSize(5)
                .extracting("numero")
                .containsExactlyInAnyOrder("M1", "M2", "M3", "M4", "M5");
    }
}
