package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Role}.
 *
 * <p>Proporciona operaciones CRUD básicas para gestionar los registros de roles en la base de datos.</p>
 *
 * <p>Este repositorio hereda las funcionalidades básicas de {@link JpaRepository},
 * que incluye operaciones para crear, leer, actualizar y eliminar registros de {@link Role}.</p>
 */
@Repository
public interface RepoRole extends JpaRepository<Role, Integer> {
}
