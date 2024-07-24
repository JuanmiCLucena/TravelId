package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Precio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoPrecio extends JpaRepository<Precio, Integer> {
}
