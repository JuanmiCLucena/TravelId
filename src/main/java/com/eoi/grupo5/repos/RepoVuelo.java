package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Vuelo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoVuelo extends JpaRepository<Vuelo, Integer> {
    Page<Vuelo> findAll(@Nullable Specification<Vuelo> spec, @Nonnull Pageable pageable);
}
