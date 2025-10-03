package com.restaurapp.demo.dto;

import com.restaurapp.demo.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CreateUserDto {

    @NotBlank
    private String nombre;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private Role rol;     // Role.ADMIN | MESERO | COCINERO | CAJERO

    // Opcional en creacion. Si viene null, el servicio lo pone true.
    private Boolean activo;
}
