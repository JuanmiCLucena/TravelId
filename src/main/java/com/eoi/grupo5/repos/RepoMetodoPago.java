package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.MetodoPago;
import com.eoi.grupo5.modelos.Pago;
import com.eoi.grupo5.modelos.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoMetodoPago extends JpaRepository<MetodoPago, Integer> {

}
