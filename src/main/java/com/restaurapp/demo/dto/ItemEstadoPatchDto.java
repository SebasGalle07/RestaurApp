package com.restaurapp.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record ItemEstadoPatchDto(
        @NotBlank String estado_preparacion // PENDIENTE | EN_PREPARACION | LISTO
) {}
