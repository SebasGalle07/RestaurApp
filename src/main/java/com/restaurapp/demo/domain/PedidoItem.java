package com.restaurapp.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "pedido_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private MenuItem itemMenu;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unit", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_preparacion", nullable = false, length = 20)
    private ItemEstado estadoPreparacion = ItemEstado.PENDIENTE;

    @Column(columnDefinition = "text")
    private String notas;
}
