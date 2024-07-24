package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Actividad;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
public interface RepoActividad extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {

    Page<Actividad> findAll(@Nullable Specification<Actividad> spec, @Nonnull Pageable pageable);

}
