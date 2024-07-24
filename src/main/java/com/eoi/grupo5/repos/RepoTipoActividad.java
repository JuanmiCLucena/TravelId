package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.TipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoTipoActividad extends JpaRepository<TipoActividad, Integer> {
}
