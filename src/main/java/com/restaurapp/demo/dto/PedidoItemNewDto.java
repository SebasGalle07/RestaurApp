package com.restaurapp.demo.dto;

import jakarta.validation.constraints.*;

public record PedidoItemNewDto(
        @NotNull Long item_menu_id,
        @NotNull @Min(1) Integer cantidad,
        String notas
) {}
