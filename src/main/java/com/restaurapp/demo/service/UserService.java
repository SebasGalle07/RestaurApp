package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Role;
import com.restaurapp.demo.domain.User;
import com.restaurapp.demo.dto.CreateUserDto;
import com.restaurapp.demo.dto.UpdateUserDto;
import com.restaurapp.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<User> list(String rol, Boolean activo) {
        if (rol != null && activo != null) {
            Role r = Role.fromValue(rol);
            return repo.findAllByRolAndActivo(r, activo);
        } else if (rol != null) {
            Role r = Role.fromValue(rol);
            return repo.findAllByRol(r);
        } else if (activo != null) {
            return repo.findAllByActivo(activo);
        }
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public User get(UUID id) {
        return repo.findById(id).orElseThrow();
    }

    @Transactional
    public User create(CreateUserDto dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + dto.getEmail());
        }
        User u = User.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .rol(dto.getRol())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .activo(true)
                .build();
        return repo.save(u);
    }

    @Transactional
    public User update(UUID id, UpdateUserDto dto) {
        User u = get(id);
        if (dto.getRol() != null) u.setRol(dto.getRol());
        if (dto.getActivo() != null) u.setActivo(dto.getActivo());
        return repo.save(u);
    }

    @Transactional
    public void delete(UUID id) {
        User u = get(id);
        u.setActivo(false); // borrado lógico
        repo.save(u);
    }
}
