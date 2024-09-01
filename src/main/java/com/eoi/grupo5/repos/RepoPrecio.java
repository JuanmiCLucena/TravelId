package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Precio;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Precio}.
 *
 * <p>Proporciona operaciones CRUD básicas para gestionar los registros de precios en la base de datos.</p>
 *
 * <p>Incluye métodos para la paginación de registros de precios.</p>
 */
@Repository
public interface RepoPrecio extends JpaRepository<Precio, Integer> {

    /**
     * Encuentra todos los precios con paginación.
     *
     * @param pageable Información de paginación.
     * @return Una página de objetos {@link Precio}.
     */
    Page<Precio> findAll(@Nonnull Pageable pageable);
}
