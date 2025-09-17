// MenuCreateDto.java
package com.restaurapp.demo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MenuCreateDto(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 1000) String descripcion,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precio,
        @NotNull Long categoria_id,
        @NotNull Boolean activo
) {}
