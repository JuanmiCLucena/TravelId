package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoVuelo extends JpaRepository<Vuelo, Integer> {
}
