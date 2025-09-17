package com.restaurapp.demo.dto;

import com.restaurapp.demo.domain.Role;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String nombre;
    private String email;
    private Role rol;
    private boolean activo;
}
