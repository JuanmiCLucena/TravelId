package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Localizacion;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Localizacion}.
 *
 * <p>Proporciona operaciones CRUD para gestionar las localizaciones en la base de datos,
 * así como consultas personalizadas para la búsqueda por nombre.</p>
 */
@Repository
public interface RepoLocalizacion extends JpaRepository<Localizacion, Integer> {

    /**
     * Encuentra todas las localizaciones de forma paginada.
     *
     * @param pageable Información de la paginación.
     * @return Página con las localizaciones encontradas.
     */
    Page<Localizacion> findAll(@Nonnull Pageable pageable);

    /**
     * Busca localizaciones cuyo nombre contenga la cadena especificada, ignorando mayúsculas y minúsculas.
     *
     *
     * @param nombre Cadena a buscar dentro del nombre de las localizaciones.
     * @return Lista de localizaciones que coinciden con la búsqueda.
     */
    List<Localizacion> findByNombreContainingIgnoreCase(String nombre);
}
