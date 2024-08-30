package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.modelos.ReservaActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link ReservaActividad}.
 *
 * <p>Proporciona operaciones CRUD básicas para gestionar los registros de reservas de actividades en la base de datos.</p>
 *
 * <p>Incluye un método para encontrar una reserva de actividad específica asociada a una reserva y una actividad.</p>
 */
@Repository
public interface RepoReservaActividad extends JpaRepository<ReservaActividad, Integer> {

    /**
     * Encuentra una instancia de {@link ReservaActividad} asociada a una reserva y una actividad específicas.
     *
     * @param reserva La reserva asociada.
     * @param actividad La actividad asociada.
     * @return Un {@link Optional} que contiene la instancia de {@link ReservaActividad} si existe, o {@code empty()} si no se encuentra.
     */
    Optional<ReservaActividad> findByReservaAndActividad(Reserva reserva, Actividad actividad);
}
