package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoHabitacion extends JpaRepository<Habitacion, Integer> {
}
