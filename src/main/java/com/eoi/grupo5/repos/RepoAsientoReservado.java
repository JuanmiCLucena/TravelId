package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.AsientoReservado;
import com.eoi.grupo5.modelos.AsientoReservadoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoAsientoReservado extends JpaRepository<AsientoReservado, AsientoReservadoId> {
}
