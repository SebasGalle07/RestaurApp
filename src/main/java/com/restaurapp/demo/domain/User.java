package com.restaurapp.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    // Guardar siempre el hash de la contraseña
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Convert(converter = RoleAttributeConverter.class)
    @Column(nullable = false)
    private Role rol;

    @Column(nullable = false)
    private boolean activo = true;
}
