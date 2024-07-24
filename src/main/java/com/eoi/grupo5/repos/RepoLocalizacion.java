package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Localizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoLocalizacion extends JpaRepository<Localizacion, Integer> {
}
