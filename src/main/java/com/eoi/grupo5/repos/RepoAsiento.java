package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoAsiento extends JpaRepository<Asiento, Integer> {
}
