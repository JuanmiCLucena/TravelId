package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Asiento;
import com.eoi.grupo5.modelos.Habitacion;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoAsiento extends JpaRepository<Asiento, Integer> {
    Page<Asiento> findAll(@Nonnull Pageable pageable);
}
