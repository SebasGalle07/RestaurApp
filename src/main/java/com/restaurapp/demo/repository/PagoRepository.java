package com.restaurapp.demo.repository;

import com.restaurapp.demo.domain.Pago;
import com.restaurapp.demo.domain.PagoEstado;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByPedidoId(Long pedidoId);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.pedido.id = :pedidoId AND p.estado = :estado")
    BigDecimal sumByPedidoAndEstado(@Param("pedidoId") Long pedidoId, @Param("estado") PagoEstado estado);
}
