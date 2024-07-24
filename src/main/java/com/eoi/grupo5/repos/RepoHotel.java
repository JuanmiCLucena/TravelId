package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoHotel extends JpaRepository<Hotel, Integer> {
}
