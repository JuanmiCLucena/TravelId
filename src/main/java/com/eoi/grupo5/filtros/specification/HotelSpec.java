package com.eoi.grupo5.filtros.specification;

import com.eoi.grupo5.filtros.FiltroHoteles;
import com.eoi.grupo5.modelos.Hotel;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * The type Hotel spec.
 */
public class HotelSpec {

    private HotelSpec() {
    }

    /**
     * Filtrar por specification usando FiltroHoteles.
     *
     * @param filtroHoteles the filtro hoteles
     * @return the specification
     */
    public static Specification<Hotel> filtrarPor(FiltroHoteles filtroHoteles) {
        return Specification
                .where(tieneTipoHabitacion(filtroHoteles.tipoHabitacionId()))
                .and(tieneCapacidadHabitacion(filtroHoteles.capacidadHabitacion()))
                .and(tieneCategoria(filtroHoteles.categoria()))
                .and(tieneLocalizacionNombre(filtroHoteles.localizacionNombre()));
    }

    /**
     * Crea una especificación que filtra los hoteles por ID de tipo de habitación.
     *
     * @param tipoHabitacionId El ID del tipo de habitación a filtrar. Si es {@code null}, no se aplica filtro.
     * @return Una especificación que filtra los hoteles por ID de tipo de habitación.
     */
    private static Specification<Hotel> tieneTipoHabitacion(Integer tipoHabitacionId) {
        return (root, query, cb) -> tipoHabitacionId == null
                ? cb.conjunction()
                : cb.equal(root.join("habitaciones").get("tipo").get("id"), tipoHabitacionId);
    }

    /**
     * Crea una especificación que filtra los hoteles por capacidad mínima de las habitaciones.
     *
     * @param capacidadHabitacion La capacidad mínima a filtrar. Si es {@code null}, no se aplica filtro.
     * @return Una especificación que filtra los hoteles por capacidad mínima de las habitaciones.
     */
    private static Specification<Hotel> tieneCapacidadHabitacion(Integer capacidadHabitacion) {
        return (root, query, cb) -> capacidadHabitacion == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.join("habitaciones").get("capacidad"), capacidadHabitacion);
    }

    /**
     * Crea una especificación que filtra los hoteles por categoría.
     *
     * @param categoria La categoría del hotel a filtrar. Si es {@code null}, no se aplica filtro.
     * @return Una especificación que filtra los hoteles por categoría.
     */
    private static Specification<Hotel> tieneCategoria(Byte categoria) {
        return (root, query, cb) -> categoria == null
                ? cb.conjunction()
                : cb.equal(root.get("categoria"), categoria);
    }

    /**
     * Crea una especificación que filtra los hoteles por el nombre de la localización.
     *
     * @param localizacionNombre El nombre de la localización a filtrar. Si es {@code null} o vacío, no se aplica filtro.
     * @return Una especificación que filtra los hoteles por el nombre de la localización.
     */
    private static Specification<Hotel> tieneLocalizacionNombre(String localizacionNombre) {
        return (root, query, cb) -> localizacionNombre == null || localizacionNombre.isEmpty()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("localizacion").get("nombre")), "%" + localizacionNombre.toLowerCase() + "%");
    }
}
