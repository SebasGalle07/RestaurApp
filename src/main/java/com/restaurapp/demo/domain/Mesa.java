package com.restaurapp.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mesas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mesa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String numero;
}
