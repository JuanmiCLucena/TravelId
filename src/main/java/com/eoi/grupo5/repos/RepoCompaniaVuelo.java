package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.CompaniaVuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoCompaniaVuelo extends JpaRepository<CompaniaVuelo, Integer> {
}
