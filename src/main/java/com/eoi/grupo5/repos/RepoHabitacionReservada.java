package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.HabitacionReservada;
import com.eoi.grupo5.modelos.HabitacionReservadaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoHabitacionReservada extends JpaRepository<HabitacionReservada, HabitacionReservadaId> {
}
