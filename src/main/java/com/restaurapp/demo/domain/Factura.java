package com.restaurapp.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas", uniqueConstraints = {
        @UniqueConstraint(columnNames = "numero")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Factura {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cada pedido solo puede tener una factura
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    // Lo generamos luego de persistir (basado en id)
    @Column(nullable = false, length = 20)
    private String numero;

    @CreationTimestamp
    @Column(name = "fecha_emision", updatable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
}
