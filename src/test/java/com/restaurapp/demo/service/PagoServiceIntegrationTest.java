package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.PagoCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/test-data.sql")
@Transactional
class PagoServiceIntegrationTest {

    @Autowired
    private PagoService pagoService;

    @Test
    void calcularSaldoPendienteConsideraPagosAplicados() {
        var saldo = pagoService.calcularSaldoPendiente(6L);
        assertThat(saldo).isEqualByComparingTo("27000");
    }

    @Test
    void crearPagoReduceSaldoPendiente() {
        var result = pagoService.crear(7L, new PagoCreateDto(new BigDecimal("10000"), "EFECTIVO"));
        assertThat(result.id()).isNotNull();
        assertThat(result.cambio()).isEqualByComparingTo("0");

        var saldo = pagoService.calcularSaldoPendiente(7L);
        assertThat(saldo).isEqualByComparingTo("10000");
    }

    @Test
    void crearPagoMayorAlSaldoDevuelveCambioEnEfectivo() {
        var dto = new PagoCreateDto(new BigDecimal("50000"), "EFECTIVO");
        var result = pagoService.crear(7L, dto);
        assertThat(result.cambio()).isGreaterThan(BigDecimal.ZERO);
    }
}
