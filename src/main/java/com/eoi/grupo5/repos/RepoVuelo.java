package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Vuelo;
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
 * Repositorio para la entidad {@link Vuelo}.
 *
 * <p>Este repositorio proporciona acceso a las operaciones CRUD para la gestión de vuelos, incluyendo búsquedas
 * especializadas utilizando especificaciones y consultas personalizadas.</p>
 *
 * <p>Hereda las funcionalidades de {@link JpaRepository}, que permite realizar operaciones básicas como crear, leer,
 * actualizar y eliminar registros de {@link Vuelo}.</p>
 */
@Repository
public interface RepoVuelo extends JpaRepository<Vuelo, Integer> {

    /**
     * Encuentra todos los vuelos aplicando una especificación opcional y paginación.
     *
     * @param spec Una especificación opcional para filtrar los resultados. Puede ser {@code null}.
     * @param pageable Objeto que define la página actual y el tamaño de la página.
     * @return Una página de {@link Vuelo} que cumple con la especificación dada y los parámetros de paginación.
     */
    Page<Vuelo> findAll(@Nullable Specification<Vuelo> spec, @Nonnull Pageable pageable);

    /**
     * Encuentra todos los vuelos disponibles a partir de la fecha actual.
     *
     * <p>Un vuelo se considera disponible si tiene al menos un asiento libre en el que no existen reservas
     * en el rango de fechas del vuelo.</p>
     *
     * @param fechaActual La fecha y hora actuales que se utilizan para filtrar los vuelos.
     * @return Una lista de {@link Vuelo} disponibles que cumplen con el criterio de disponibilidad.
     */
    @Query(
            "SELECT v FROM Vuelo v " +
                    "WHERE v.fechaSalida > :fechaActual " +
                    "AND v.fechaLlegada > :fechaActual " +
                    "AND EXISTS (" +
                    "    SELECT a FROM v.asientos a " +
                    "    WHERE NOT EXISTS (" +
                    "        SELECT r FROM a.reservas r " +
                    "        WHERE r.fechaInicio < v.fechaLlegada " +
                    "        AND r.fechaFin > v.fechaSalida " +
                    "    )" +
                    ")"
    )
    List<Vuelo> findVuelosDisponibles(@Param("fechaActual") LocalDateTime fechaActual);

}
