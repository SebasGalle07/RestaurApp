package com.restaurapp.demo.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String nombre;
    private String rol;          // en minusculas: "admin", "mesero", etc.
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
