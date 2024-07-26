package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Asiento;
import com.eoi.grupo5.modelos.Localizacion;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoLocalizacion extends JpaRepository<Localizacion, Integer> {
    Page<Localizacion> findAll(@Nonnull Pageable pageable);
}
