package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.TipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link TipoActividad}.
 *
 * <p>Proporciona operaciones CRUD básicas para gestionar los registros de tipos de actividades en la base de datos.</p>
 *
 * <p>Este repositorio hereda las funcionalidades básicas de {@link JpaRepository},
 * que incluye operaciones para crear, leer, actualizar y eliminar registros de {@link TipoActividad}.</p>
 */
@Repository
public interface RepoTipoActividad extends JpaRepository<TipoActividad, Integer> {
}
