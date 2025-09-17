// CategoriaService.java
package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.CategoriaCreateDto;
import com.restaurapp.demo.dto.CategoriaDto;
import com.restaurapp.demo.dto.CategoriaUpdateDto;

import java.util.List;

public interface CategoriaService {
    List<CategoriaDto> listar();
    Long crear(CategoriaCreateDto dto);
    CategoriaDto detalle(Long id);
    void actualizar(Long id, CategoriaUpdateDto dto);
    void eliminar(Long id);
}
