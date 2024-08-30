package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Pago}.
 *
 * <p>Proporciona las operaciones CRUD básicas para gestionar los registros de pagos en la base de datos.</p>
 */
@Repository
public interface RepoPago extends JpaRepository<Pago, Integer> {

}
