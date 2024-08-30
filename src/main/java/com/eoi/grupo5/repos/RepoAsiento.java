package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Asiento;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Asiento}.
 *
 * <p>Este repositorio gestiona el acceso a los datos de la tabla de asientos y permite
 * realizar operaciones CRUD básicas. También soporta la paginación de resultados.</p>
 */
@Repository
public interface RepoAsiento extends JpaRepository<Asiento, Integer> {

    /**
     * Recupera todos los asientos de manera paginada.
     *
     * <p>Este método se utiliza para obtener una lista paginada de asientos. La paginación
     * permite manejar grandes volúmenes de datos de manera eficiente.</p>
     *
     * @param pageable Objeto de paginación que define la página a obtener y el tamaño de la página.
     * @return Un {@link Page} de asientos según la paginación especificada.
     */
    Page<Asiento> findAll(@Nonnull Pageable pageable);
}
