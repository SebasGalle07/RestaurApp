package com.restaurapp.demo.dto;

import com.restaurapp.demo.domain.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    private String nombre;
    private String email;
    private Role rol;
    private String password;
}
