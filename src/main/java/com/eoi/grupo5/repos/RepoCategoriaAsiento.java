package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.CategoriaAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link CategoriaAsiento}.
 *
 * <p>Este repositorio proporciona métodos para realizar operaciones CRUD sobre la tabla
 * que almacena las categorías de los asientos. Hereda funcionalidades básicas de JPA.</p>
 */
@Repository
public interface RepoCategoriaAsiento extends JpaRepository<CategoriaAsiento, Integer> {
}
