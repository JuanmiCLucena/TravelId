package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoImagen extends JpaRepository<Imagen, Integer> {
}
