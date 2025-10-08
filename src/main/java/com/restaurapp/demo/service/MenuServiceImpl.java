package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Categoria;
import com.restaurapp.demo.domain.MenuItem;
import com.restaurapp.demo.dto.MenuCreateDto;
import com.restaurapp.demo.dto.MenuDto;
import com.restaurapp.demo.dto.MenuPatchDto;
import com.restaurapp.demo.repository.CategoriaRepository;
import com.restaurapp.demo.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final MenuRepository repo;
    private final CategoriaRepository categoriaRepo;

    /** Map a DTO evitando NPE si la categoría viene null en algún registro. */
    private MenuDto toDto(MenuItem e) {
        Long   catId     = (e.getCategoria() != null) ? e.getCategoria().getId()      : null;
        String catNombre = (e.getCategoria() != null) ? e.getCategoria().getNombre() : null;

        return new MenuDto(
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                e.getPrecio(),
                catId,
                catNombre,
                e.getActivo()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuDto> listar(Long categoriaId, Boolean activo, String q, int page, int size, String sort) {
        // sort esperado "campo,asc|desc" con defaults seguros
        String prop = "id"; // cambia a "nombre" si prefieres ordenar por nombre
        Sort.Direction dir = Sort.Direction.ASC;

        if (sort != null && !sort.isBlank()) {
            String[] s = sort.split(",", 2);
            if (s.length >= 1 && !s[0].isBlank()) prop = s[0].trim();
            if (s.length == 2) {
                try { dir = Sort.Direction.fromString(s[1].trim()); } catch (Exception ignored) {}
            }
        }
        // Si tu PK no se llama "id", mapea aquí (p. ej. "menuId"):
        // if ("id".equals(prop)) prop = "menuId";

        Pageable pr = PageRequest.of(page, size, Sort.by(dir, prop));
        String qNorm = (q == null || q.isBlank()) ? null : q.trim();

        try {
            return repo.buscar(categoriaId, activo, qNorm, pr).map(this::toDto);
        } catch (Exception ex) {
            // Log bien visible para ubicar rápidamente la causa
            log.error("Fallo en repo.buscar(categoriaId={}, activo={}, qNorm={}, pageable={}): {}",
                    categoriaId, activo, qNorm, pr, ex.toString(), ex);

            // --- Fallback de diagnóstico (temporal): probar sin filtros ni query custom ---
            Page<MenuItem> all = repo.findAll(pr);
            log.warn("Fallback findAll ejecutado. Registros devueltos: {}", all.getNumberOfElements());
            return all.map(this::toDto);
        }
    }

    @Override
    @Transactional
    public Long crear(MenuCreateDto dto) {
        Categoria cat = categoriaRepo.findById(dto.categoria_id())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));

        MenuItem e = MenuItem.builder()
                .nombre(dto.nombre().trim())
                .descripcion(dto.descripcion())
                .precio(dto.precio())
                .categoria(cat)
                .activo(dto.activo())
                .build();

        return repo.save(e).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public MenuDto detalle(Long id) {
        MenuItem e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));
        return toDto(e);
    }

    @Override
    @Transactional
    public void patch(Long id, MenuPatchDto dto) {
        MenuItem e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));

        if (dto.nombre() != null)      e.setNombre(dto.nombre().trim());
        if (dto.descripcion() != null) e.setDescripcion(dto.descripcion());
        if (dto.precio() != null)      e.setPrecio(dto.precio());
        if (dto.activo() != null)      e.setActivo(dto.activo());

        if (dto.categoria_id() != null) {
            Categoria cat = categoriaRepo.findById(dto.categoria_id())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
            e.setCategoria(cat);
        }
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        MenuItem e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));
        repo.delete(e);
    }
}
