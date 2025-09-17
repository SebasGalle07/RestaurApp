package com.restaurapp.demo.service;

import com.restaurapp.demo.dto.MesaDto;
import com.restaurapp.demo.repository.MesaRepository;
import com.restaurapp.demo.service.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {
    private final MesaRepository repo;

    @Override
    public List<MesaDto> listar() {
        return repo.findAll().stream()
                .map(m -> new MesaDto(m.getId(), m.getNumero()))
                .toList();
    }
}
