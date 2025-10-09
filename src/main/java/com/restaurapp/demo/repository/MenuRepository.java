package com.restaurapp.demo.repository;

import com.restaurapp.demo.domain.MenuItem;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<MenuItem, Long> {

    @Query("""
    SELECT m FROM MenuItem m
    WHERE (:categoriaId IS NULL OR m.categoria.id = :categoriaId)
      AND (:activo IS NULL OR m.activo = :activo)
      AND (:q IS NULL OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :q, '%')))
    """)
    Page<MenuItem> buscar(@Param("categoriaId") Long categoriaId,
                          @Param("activo") Boolean activo,
                          @Param("q") String q,
                          Pageable pageable);
}
