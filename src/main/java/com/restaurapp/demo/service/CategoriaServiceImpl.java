// CategoriaServiceImpl.java
package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Categoria;
import com.restaurapp.demo.dto.CategoriaCreateDto;
import com.restaurapp.demo.dto.CategoriaDto;
import com.restaurapp.demo.dto.CategoriaUpdateDto;
import com.restaurapp.demo.repository.CategoriaRepository;
import com.restaurapp.demo.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository repo;

    private CategoriaDto toDto(Categoria c) {
        return new CategoriaDto(c.getId(), c.getNombre(), c.getDescripcion());
    }

    @Override
    public List<CategoriaDto> listar() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public Long crear(CategoriaCreateDto dto) {
        if (repo.existsByNombreIgnoreCase(dto.nombre()))
            throw new DataIntegrityViolationException("Nombre de categoría ya existe");
        var entity = Categoria.builder()
                .nombre(dto.nombre().trim())
                .descripcion(dto.descripcion())
                .build();
        return repo.save(entity).getId();
    }

    @Override
    public CategoriaDto detalle(Long id) {
        var c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        return toDto(c);
    }

    @Override
    @Transactional
    public void actualizar(Long id, CategoriaUpdateDto dto) {
        var c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        if (!c.getNombre().equalsIgnoreCase(dto.nombre()) && repo.existsByNombreIgnoreCase(dto.nombre()))
            throw new DataIntegrityViolationException("Nombre de categoría ya existe");
        c.setNombre(dto.nombre().trim());
        c.setDescripcion(dto.descripcion());
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        var c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        repo.delete(c);
        // Si tu BD tiene FK desde menu(categoria_id), la eliminación fallará con DataIntegrityViolationException → 409 si tienes un ControllerAdvice.
    }
}
