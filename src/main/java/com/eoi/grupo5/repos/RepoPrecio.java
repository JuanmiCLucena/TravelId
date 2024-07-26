package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Precio;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoPrecio extends JpaRepository<Precio, Integer> {
    Page<Precio> findAll(@Nonnull Pageable pageable);
}
