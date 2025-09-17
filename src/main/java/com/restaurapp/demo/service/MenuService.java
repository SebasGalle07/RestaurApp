// MenuService.java
package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.MenuCreateDto;
import com.restaurapp.demo.dto.MenuDto;
import com.restaurapp.demo.dto.MenuPatchDto;
import org.springframework.data.domain.Page;

public interface MenuService {
    Page<MenuDto> listar(Long categoriaId, Boolean activo, String q, int page, int size, String sort);
    Long crear(MenuCreateDto dto);
    MenuDto detalle(Long id);
    void patch(Long id, MenuPatchDto dto);
    void eliminar(Long id);
}
