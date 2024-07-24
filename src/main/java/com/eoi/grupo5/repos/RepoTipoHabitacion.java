package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoTipoHabitacion extends JpaRepository<TipoHabitacion, Integer> {
}
