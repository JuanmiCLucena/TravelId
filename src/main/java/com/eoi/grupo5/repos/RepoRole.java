package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoRole extends JpaRepository<Role, Integer> {
}
