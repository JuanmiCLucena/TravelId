package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad {@link Reserva}.
 *
 * <p>Proporciona operaciones CRUD básicas para gestionar los registros de reservas en la base de datos.</p>
 *
 * <p>Incluye métodos para recuperar reservas basadas en habitaciones y usuarios.</p>
 */
@Repository
public interface RepoReserva extends JpaRepository<Reserva, Integer> {

    /**
     * Encuentra todas las reservas que incluyen una habitación específica.
     *
     * @param idHabitacion El identificador de la habitación.
     * @return Una lista de objetos {@link Reserva} que contienen la habitación especificada.
     */
    List<Reserva> findByHabitacionesReservadasId(Integer idHabitacion);

    /**
     * Encuentra todas las reservas asociadas a un usuario con paginación.
     *
     * @param usuario El usuario cuyas reservas se desean recuperar.
     * @param pageable Información de paginación.
     * @return Una página de objetos {@link Reserva} asociada al usuario especificado.
     */
    Page<Reserva> findByUsu(Usuario usuario, Pageable pageable);
}
