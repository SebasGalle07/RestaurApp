package com.restaurapp.demo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PagoCreateDto(
        @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal monto,
        @NotBlank String metodo // EFECTIVO | TARJETA | QR | TRANSFERENCIA
) {}
