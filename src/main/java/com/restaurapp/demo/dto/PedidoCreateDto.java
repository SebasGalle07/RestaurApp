package com.restaurapp.demo.dto;
import com.restaurapp.demo.dto.PedidoItemNewDto;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

public record PedidoCreateDto(
        @NotNull Long mesa_id,
        @NotNull UUID mesero_id,
        String notas,
        @NotNull @Size(min = 1) List<PedidoItemNewDto> items
) {}
