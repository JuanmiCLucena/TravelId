package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.CategoriaAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoCategoriaAsiento extends JpaRepository<CategoriaAsiento, Integer> {
}
