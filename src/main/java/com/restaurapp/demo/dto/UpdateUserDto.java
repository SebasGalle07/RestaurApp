package com.restaurapp.demo.dto;

import com.restaurapp.demo.domain.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    private Role rol;
    private Boolean activo;
}
