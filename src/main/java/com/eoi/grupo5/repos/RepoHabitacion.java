package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Habitacion;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Habitacion}.
 *
 * <p>Proporciona métodos CRUD para gestionar habitaciones en la base de datos,
 * además de la funcionalidad para la paginación de los resultados.</p>
 */
@Repository
public interface RepoHabitacion extends JpaRepository<Habitacion, Integer> {

    /**
     * Recupera una lista paginada de habitaciones.
     *
     * @param pageable Parámetros de paginación, como el número de página y el tamaño de la página.
     * @return Una página de habitaciones según los parámetros de paginación.
     */
    Page<Habitacion> findAll(@Nonnull Pageable pageable);
}
