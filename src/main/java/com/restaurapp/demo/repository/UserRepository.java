package com.restaurapp.demo.repository;

import com.restaurapp.demo.domain.Role;
import com.restaurapp.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByCodigo(Long codigo);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Para login solo usuarios activos (opcional pero util)
    Optional<User> findByEmailAndActivoTrue(String email);

    // Soporte a filtros del endpoint /users
    List<User> findAllByRol(Role rol);
    List<User> findAllByActivo(boolean activo);
    List<User> findAllByRolAndActivo(Role rol, boolean activo);
}
