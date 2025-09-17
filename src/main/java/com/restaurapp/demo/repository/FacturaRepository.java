package com.restaurapp.demo.repository;

import com.restaurapp.demo.domain.Factura;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    boolean existsByPedidoId(Long pedidoId);
    Optional<Factura> findByPedidoId(Long pedidoId);

    @Query("""
    SELECT f FROM Factura f
    WHERE (:mesaId IS NULL OR f.pedido.mesa.id = :mesaId)
      AND (:meseroId IS NULL OR f.pedido.meseroId = :meseroId)
      AND (:desde IS NULL OR f.fechaEmision >= :desde)
      AND (:hasta IS NULL OR f.fechaEmision < :hasta)
    """)
    Page<Factura> buscar(@Param("mesaId") Long mesaId,
                         @Param("meseroId") UUID meseroId, // Cambiar de Long a UUID
                         @Param("desde") LocalDateTime desde,
                         @Param("hasta") LocalDateTime hasta,
                         Pageable pageable);
}
