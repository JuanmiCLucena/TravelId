package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoPais extends JpaRepository<Pais, Integer> {
}
