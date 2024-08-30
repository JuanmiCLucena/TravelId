package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Hotel;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad {@link Hotel}.
 *
 * <p>Proporciona métodos CRUD para gestionar hoteles en la base de datos,
 * además de métodos personalizados para búsqueda de hoteles disponibles
 * y paginación de resultados.</p>
 */
@Repository
public interface RepoHotel extends JpaRepository<Hotel, Integer> {

    /**
     * Recupera una lista paginada de hoteles basada en las especificaciones dadas.
     *
     * @param spec     Especificación de filtros para la consulta, puede ser nula.
     * @param pageable Parámetros de paginación, como el número de página y el tamaño de la página.
     * @return Una página de hoteles según los filtros y parámetros de paginación.
     */
    Page<Hotel> findAll(@Nullable Specification<Hotel> spec, @Nonnull Pageable pageable);

    /**
     * Encuentra todos los hoteles que tienen al menos una habitación libre en el rango de fechas especificado.
     *
     * @param fechaInicio Fecha de inicio del periodo de búsqueda.
     * @param fechaFin    Fecha de fin del periodo de búsqueda.
     * @return Una lista de hoteles disponibles en el rango de fechas proporcionado.
     */
    @Query(
            "SELECT h FROM Hotel h " +
                    "WHERE EXISTS (" +
                    "    SELECT r FROM h.habitaciones r " +
                    "    WHERE NOT EXISTS (" +
                    "        SELECT res FROM r.reservas res " +
                    "        WHERE res.fechaInicio < :fechaFin " +
                    "        AND res.fechaFin > :fechaInicio " +
                    "    )" +
                    ")"
    )
    List<Hotel> findHotelesDisponibles(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
