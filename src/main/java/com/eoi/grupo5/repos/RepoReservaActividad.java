package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.modelos.ReservaActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoReservaActividad extends JpaRepository<ReservaActividad, Integer> {

    // Buscar por reserva y actividad
    Optional<ReservaActividad> findByReservaAndActividad(Reserva reserva, Actividad actividad);

}
