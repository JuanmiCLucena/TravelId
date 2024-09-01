package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link MetodoPago}.
 *
 * <p>Proporciona operaciones CRUD para gestionar los métodos de pago en la base de datos.</p>
 */
@Repository
public interface RepoMetodoPago extends JpaRepository<MetodoPago, Integer> {

}
