package com.restaurapp.demo.controller;

import com.restaurapp.demo.dto.MesaDto;
import com.restaurapp.demo.service.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/mesas")
@RequiredArgsConstructor
public class MesasController {
    private final MesaService mesaService;

    // GET /mesas→ { "success": true, "data": [...] }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MESERO','COCINERO','CAJERO')")
    public Map<String, Object> listar() {
        List<MesaDto> data = mesaService.listar();
        return Map.of("success", true, "data", data);
    }
}
