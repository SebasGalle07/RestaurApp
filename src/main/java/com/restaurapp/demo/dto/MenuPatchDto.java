// MenuPatchDto.java
package com.restaurapp.demo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MenuPatchDto(
        @Size(max = 150) String nombre,
        @Size(max = 1000) String descripcion,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal precio,
        Long categoria_id,
        Boolean activo
) {}
