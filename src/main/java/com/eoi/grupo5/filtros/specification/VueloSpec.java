package com.eoi.grupo5.filtros.specification;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.filtros.FiltroVuelos;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class VueloSpec {

    private VueloSpec() {
        // Constructor privado para evitar instanciación.
    }

    public static Specification<Vuelo> filtrarPor(FiltroVuelos filtroVuelos) {

        LocalDateTime fechaActual = LocalDateTime.now();

        return Specification
                .where(tieneOrigen(filtroVuelos.origenNombre()))
                .and(tieneDestino(filtroVuelos.destinoNombre()))
                .and(tieneFechaSalidaMayorQueInicio(filtroVuelos.fechaInicio()))
                .and(tieneFechaLlegadaMenorQueFin(filtroVuelos.fechaFin()))
                .and(noHaTerminado(fechaActual));
    }

    private static Specification<Vuelo> tieneOrigen(String origenNombre) {
        return (root, query, cb) -> origenNombre == null || origenNombre.isEmpty()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("origen").get("nombre")), "%" + origenNombre.toLowerCase() + "%");
    }

    private static Specification<Vuelo> tieneDestino(String destinoNombre) {
        return (root, query, cb) -> destinoNombre == null || destinoNombre.isEmpty()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("destino").get("nombre")), "%" + destinoNombre.toLowerCase() + "%");
    }

    private static Specification<Vuelo> tieneFechaSalidaMayorQueInicio(LocalDateTime fechaInicio) {
        return (root, query, cb) -> fechaInicio == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fechaSalida"), fechaInicio);
    }

    private static Specification<Vuelo> tieneFechaLlegadaMenorQueFin(LocalDateTime fechaFin) {
        return (root, query, cb) -> fechaFin == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("fechaLlegada"), fechaFin);
    }

    /**
     * Crea una especificación que filtra los vuelos cuya fecha de fin es mayor que la fecha actual.
     *
     * @param fechaActual La fecha y hora actuales.
     * @return Una especificación que filtra los vuelos que aún no han terminado.
     */
    private static Specification<Vuelo> noHaTerminado(LocalDateTime fechaActual) {
        return (root, query, cb) -> cb.greaterThan(root.get("fechaLlegada"), fechaActual);
    }
}
