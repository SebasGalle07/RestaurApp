// MenuDto.java
package com.restaurapp.demo.dto;

import java.math.BigDecimal;

public record MenuDto(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Long categoria_id,
        String categoria_nombre,
        Boolean activo
) {}
