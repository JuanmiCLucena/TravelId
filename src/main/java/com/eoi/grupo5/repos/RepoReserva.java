package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.modelos.Usuario;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepoReserva extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByHabitacionesReservadasId(Integer idHabitacion);
    Page<Reserva> findByUsu(Usuario usuario, Pageable pageable);
}
