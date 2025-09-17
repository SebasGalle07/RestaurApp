package com.restaurapp.demo.dto;
import java.util.UUID;

public record PedidoPatchDto(
        UUID mesero_id,
        String notas
) {}
