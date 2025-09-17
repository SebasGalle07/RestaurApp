package com.restaurapp.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Categoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(columnDefinition = "text")
    private String descripcion;
}
