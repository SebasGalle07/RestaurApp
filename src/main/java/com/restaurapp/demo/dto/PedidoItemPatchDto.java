package com.restaurapp.demo.dto;

import jakarta.validation.constraints.*;

public record PedidoItemPatchDto(
        @Min(1) Integer cantidad,
        String notas
) {}
