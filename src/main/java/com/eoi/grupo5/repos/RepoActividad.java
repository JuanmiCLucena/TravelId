package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Actividad;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad {@link Actividad}.
 *
 * <p>Este repositorio gestiona el acceso a los datos de la tabla de actividades
 * y permite realizar operaciones CRUD, además de consultas avanzadas utilizando
 * {@link JpaSpecificationExecutor} para la creación dinámica de queries.</p>
 */
@Repository
public interface RepoActividad extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {

    /**
     * Encuentra todas las actividades que cumplen con las especificaciones dadas y las pagina.
     *
     * <p>Este método permite aplicar filtros avanzados sobre las actividades, como búsqueda por
     * criterios específicos, y devuelve un resultado paginado.</p>
     *
     * @param spec     Especificación de búsqueda que puede ser {@code null}.
     * @param pageable Objeto de paginación que define la página a obtener y el tamaño de la página.
     * @return Un {@link Page} de actividades que coinciden con la especificación y la paginación dadas.
     */
    Page<Actividad> findAll(@Nullable Specification<Actividad> spec, @Nonnull Pageable pageable);

    /**
     * Encuentra todas las actividades que están disponibles para nuevos asistentes.
     *
     * <p>Este método busca actividades que aún no han alcanzado su límite máximo de asistentes
     * y que tienen fechas de inicio y fin posteriores a la fecha actual. Los resultados se ordenan
     * por la fecha de inicio de forma ascendente.</p>
     *
     * @param fechaActual La fecha y hora actuales, utilizadas como referencia para filtrar actividades futuras.
     * @return Una lista de actividades disponibles para nuevos asistentes.
     */
    @Query(
            "SELECT a FROM Actividad a " +
                    "WHERE a.maximosAsistentes IS NOT NULL AND a.asistentesConfirmados IS NOT NULL " +
                    "AND a.maximosAsistentes > a.asistentesConfirmados " +
                    "AND a.fechaFin > :fechaActual " +
                    "AND a.fechaInicio > :fechaActual " +
                    "ORDER BY a.fechaInicio ASC")
    List<Actividad> findActividadesDisponibles(LocalDateTime fechaActual);
}
