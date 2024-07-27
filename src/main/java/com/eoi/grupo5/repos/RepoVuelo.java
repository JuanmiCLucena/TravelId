package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Vuelo;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepoVuelo extends JpaRepository<Vuelo, Integer> {
    Page<Vuelo> findAll(@Nullable Specification<Vuelo> spec, @Nonnull Pageable pageable);

    @Query(
            "SELECT v FROM Vuelo v " +
                    "WHERE v.fechaSalida > :fechaActual " +
                    "AND v.fechaLlegada > :fechaActual " +
                    "AND EXISTS (" +
                    "    SELECT a FROM v.asientos a " +
                    "    WHERE NOT EXISTS (" +
                    "        SELECT r FROM a.reservas r " +
                    "        WHERE r.fechaInicio < v.fechaLlegada " +
                    "        AND r.fechaFin > v.fechaSalida " +
                    "    )" +
                    ")"
    )
    List<Vuelo> findVuelosDisponibles(@Param("fechaActual") LocalDateTime fechaActual);


}
