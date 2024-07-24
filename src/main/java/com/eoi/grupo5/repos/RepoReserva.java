package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepoReserva extends JpaRepository<Reserva, Integer> {

}
