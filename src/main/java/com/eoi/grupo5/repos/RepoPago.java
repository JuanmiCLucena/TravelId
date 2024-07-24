package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoPago extends JpaRepository<Pago, Integer> {
}
