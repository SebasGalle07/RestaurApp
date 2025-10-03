package com.restaurapp.demo.service;

import com.restaurapp.demo.domain.Role;
import com.restaurapp.demo.domain.User;
import com.restaurapp.demo.dto.CreateUserDto;
import com.restaurapp.demo.dto.UpdateUserDto;
import com.restaurapp.demo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<User> list(String rol, Boolean activo) {
        // Filtros combinados
        if (rol != null && !rol.isBlank() && activo != null) {
            Role r = Role.fromValue(rol); // "admin"|"mesero"|... (case-insensitive)
            return repo.findAllByRolAndActivo(r, activo);
        }
        // Solo rol
        if (rol != null && !rol.isBlank()) {
            Role r = Role.fromValue(rol);
            return repo.findAllByRol(r);
        }
        // Solo activo
        if (activo != null) {
            return repo.findAllByActivo(activo);
        }
        // Sin filtros
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public User get(UUID id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public User getByCodigo(Long codigo) {
        return repo.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional
    public User create(CreateUserDto dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya esta registrado: " + dto.getEmail());
        }

        User u = User.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .rol(dto.getRol())               // DTO trae Role (no String)
                .activo(dto.getActivo() == null ? true : dto.getActivo())
                .build();

        User saved = repo.saveAndFlush(u);

        // Si 'codigo' lo asigna la BD via DEFAULT y en la entidad esta insertable=false/updatable=false,
        // refrescar para obtenerlo en el objeto devuelto:
        try {
            em.refresh(saved);
        } catch (Exception ignored) {
            // si no hay DEFAULT o no hace falta, no pasa nada
        }

        return saved;
    }

    @Transactional
    public User update(UUID id, UpdateUserDto dto) {
        User u = get(id);

        if (dto.getNombre() != null) {
            u.setNombre(dto.getNombre());
        }

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(u.getEmail())) {
            if (repo.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("El email ya esta registrado: " + dto.getEmail());
            }
            u.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRol() != null) {
            u.setRol(dto.getRol());  // DTO trae Role
        }

        if (dto.getActivo() != null) {
            u.setActivo(dto.getActivo());
        }

        return repo.save(u);
    }

    @Transactional
    public void delete(UUID id) {
        // Borrado logico (como tenias)
        User u = get(id);
        u.setActivo(false);
        repo.save(u);
    }
}
