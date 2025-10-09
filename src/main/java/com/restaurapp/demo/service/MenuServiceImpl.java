// MenuServiceImpl.java
package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Categoria;
import com.restaurapp.demo.domain.MenuItem;
import com.restaurapp.demo.dto.MenuCreateDto;
import com.restaurapp.demo.dto.MenuDto;
import com.restaurapp.demo.dto.MenuPatchDto;
import com.restaurapp.demo.repository.CategoriaRepository;
import com.restaurapp.demo.repository.MenuRepository;
import com.restaurapp.demo.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository repo;
    private final CategoriaRepository categoriaRepo;

    private MenuDto toDto(MenuItem e) {
        return new MenuDto(
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                e.getPrecio(),
                e.getCategoria().getId(),
                e.getCategoria().getNombre(),
                e.getActivo()
        );
    }

    @Override
    public Page<MenuDto> listar(Long categoriaId, Boolean activo, String q, int page, int size, String sort) {
        // sort esperado: "campo,asc|desc" (p.ej. "id,asc")
        String[] s = (sort == null || sort.isBlank()) ? new String[]{"id","asc"} : sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sortObj = Sort.by(dir, s[0]);
        PageRequest pr = PageRequest.of(page, size, sortObj);

        String qNorm = (q == null || q.isBlank()) ? null : q.trim();
        return repo.buscar(categoriaId, activo, qNorm, pr).map(this::toDto);
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
    public MenuDto detalle(Long id) {
        MenuItem e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));
        return toDto(e);
    }

    @Override
    @Transactional
    public void patch(Long id, MenuPatchDto dto) {
        MenuItem e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));
        if (dto.nombre() != null) e.setNombre(dto.nombre().trim());
        if (dto.descripcion() != null) e.setDescripcion(dto.descripcion());
        if (dto.precio() != null) e.setPrecio(dto.precio());
        if (dto.activo() != null) e.setActivo(dto.activo());
        if (dto.categoria_id() != null) {
            Categoria cat = categoriaRepo.findById(dto.categoria_id())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
            e.setCategoria(cat);
        }
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        MenuItem e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Item no encontrado"));
        repo.delete(e);
        // Si hay FK desde pedido_items(item_menu_id), la BD impedira borrar -> capturalo con tu @ControllerAdvice como 409.
    }
}
