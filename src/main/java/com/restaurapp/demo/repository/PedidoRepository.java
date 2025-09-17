package com.restaurapp.demo.repository;

import com.restaurapp.demo.domain.Pedido;
import com.restaurapp.demo.domain.PedidoEstado;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
    SELECT p FROM Pedido p
    WHERE (:mesaId IS NULL OR p.mesa.id = :mesaId)
      AND (:estado IS NULL OR p.estado = :estado)
      AND (:desde IS NULL OR p.createdAt >= :desde)
      AND (:hasta IS NULL OR p.createdAt < :hasta)
    """)
    Page<Pedido> buscar(@Param("mesaId") Long mesaId,
                        @Param("estado") PedidoEstado estado,
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta,
                        Pageable pageable);
}
