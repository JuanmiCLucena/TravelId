package com.eoi.grupo5.repos.specification;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.repos.filtros.FiltroActividades;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ActividadSpec {

    public static final String TIPO = "tipo";
    public static final String FECHA_INICIO = "fechaInicio";
    public static final String FECHA_FIN = "fechaFin";

    private ActividadSpec() {

    }

    public static Specification<Actividad> filtrarPor(FiltroActividades filtroActividades) {
        return Specification
                .where(esTipo(filtroActividades.tipoId()))
                .and(tieneFechaMayorQueInicio(filtroActividades.fechaInicio()))
                .and(tieneFechaMenorQueFin(filtroActividades.fechaFin()));
    }

    private static Specification<Actividad> esTipo(Integer tipoId) {
        return (root, query, cb) -> tipoId == null ? cb.conjunction() : cb.equal(root.get(TIPO).get("id"), tipoId);
    }

    private static Specification<Actividad> tieneFechaMayorQueInicio(LocalDateTime fechaInicio) {
        return (root, query, cb) -> fechaInicio == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get(FECHA_INICIO), fechaInicio);
    }

    private static Specification<Actividad> tieneFechaMenorQueFin(LocalDateTime fechaFin) {
        return (root, query, cb) -> fechaFin == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get(FECHA_FIN), fechaFin);
    }



}
