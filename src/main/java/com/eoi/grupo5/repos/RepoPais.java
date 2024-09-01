package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Pais}.
 *
 * <p>Proporciona operaciones CRUD básicas para gestionar los registros de países en la base de datos.</p>
 */
@Repository
public interface RepoPais extends JpaRepository<Pais, Integer> {

}
