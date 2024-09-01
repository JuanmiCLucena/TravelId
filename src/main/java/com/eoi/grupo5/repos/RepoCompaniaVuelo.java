package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.CompaniaVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link CompaniaVuelo}.
 *
 * <p>Este repositorio proporciona métodos CRUD para gestionar las compañías de vuelo
 * en la base de datos, utilizando las funcionalidades básicas de JPA.</p>
 */
@Repository
public interface RepoCompaniaVuelo extends JpaRepository<CompaniaVuelo, Integer> {
}
