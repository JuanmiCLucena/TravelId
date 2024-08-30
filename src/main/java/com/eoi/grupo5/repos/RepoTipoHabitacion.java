package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link TipoHabitacion}.
 *
 * <p>Este repositorio proporciona operaciones CRUD básicas para la gestión de registros de tipos de habitación en la base de datos.</p>
 *
 * <p>Hereda las funcionalidades de {@link JpaRepository},
 * lo que incluye operaciones para crear, leer, actualizar y eliminar registros de {@link TipoHabitacion}.</p>
 */
@Repository
public interface RepoTipoHabitacion extends JpaRepository<TipoHabitacion, Integer> {
}
