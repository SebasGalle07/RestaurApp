package com.restaurapp.demo.dto;

import com.restaurapp.demo.domain.Role;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UpdateUserDto {

    // Todos opcionales: solo se actualiza lo que venga no-null

    private String nombre;

    @Email
    private String email;

    private String password;

    private Role rol;

    private Boolean activo;
}
