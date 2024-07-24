package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.DetallesUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoDetallesUsuario extends JpaRepository<DetallesUsuario, Integer> {
}
