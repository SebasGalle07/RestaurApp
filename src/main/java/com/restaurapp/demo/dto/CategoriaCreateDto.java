// CategoriaCreateDto.java
package com.restaurapp.demo.dto;

import jakarta.validation.constraints.*;

public record CategoriaCreateDto(
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 500) String descripcion
) {}
