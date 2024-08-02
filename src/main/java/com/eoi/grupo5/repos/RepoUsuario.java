package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Usuario;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoUsuario extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Page<Usuario> findAll(@Nullable Specification<Usuario> spec, @Nonnull Pageable pageable);

    boolean existsBynombreUsuario(String username);
}
