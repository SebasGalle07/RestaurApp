package com.restaurapp.demo.repository;

import com.restaurapp.demo.domain.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<MenuItem, Long> {

    @Query(
        value = """
            select m
            from MenuItem m
            left join m.categoria c
            where (:categoriaId is null or c.id = :categoriaId)
              and (:activo      is null or m.activo = :activo)
              and (
                   :q is null
                   or lower(m.nombre) like lower(concat('%', :q, '%'))
                   or lower(coalesce(m.descripcion, '')) like lower(concat('%', :q, '%'))
              )
            """,
        countQuery = """
            select count(m)
            from MenuItem m
            left join m.categoria c
            where (:categoriaId is null or c.id = :categoriaId)
              and (:activo      is null or m.activo = :activo)
              and (
                   :q is null
                   or lower(m.nombre) like lower(concat('%', :q, '%'))
                   or lower(coalesce(m.descripcion, '')) like lower(concat('%', :q, '%'))
              )
            """
    )
    Page<MenuItem> buscar(@Param("categoriaId") Long categoriaId,
                          @Param("activo") Boolean activo,
                          @Param("q") String q,
                          Pageable pageable);
}
