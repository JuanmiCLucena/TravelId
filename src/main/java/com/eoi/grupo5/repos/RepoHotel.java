package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Hotel;
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
public interface RepoHotel extends JpaRepository<Hotel, Integer> {
    Page<Hotel> findAll(@Nullable Specification<Hotel> spec, @Nonnull Pageable pageable);

    @Query(
            "SELECT h FROM Hotel h " +
                    "WHERE EXISTS (" +
                    "    SELECT r FROM h.habitaciones r " +
                    "    WHERE NOT EXISTS (" +
                    "        SELECT res FROM r.reservas res " +
                    "        WHERE res.fechaInicio < :fechaFin " +
                    "        AND res.fechaFin > :fechaInicio " +
                    "    )" +
                    ")"
    )
    List<Hotel> findHotelesDisponibles(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);

}
