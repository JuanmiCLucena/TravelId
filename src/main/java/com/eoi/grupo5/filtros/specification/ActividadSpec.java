package com.eoi.grupo5.filtros.specification;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.filtros.FiltroActividades;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * The type Actividad spec.
 */
public class ActividadSpec {

    /**
     * The constant TIPO.
     */
    public static final String TIPO = "tipo";
    /**
     * The constant FECHA_INICIO.
     */
    public static final String FECHA_INICIO = "fechaInicio";
    /**
     * The constant FECHA_FIN.
     */
    public static final String FECHA_FIN = "fechaFin";

    public static final String LOCALIZACION_NOMBRE = "localizacionNombre";

    private ActividadSpec() {

    }

    /**
     * Filtrar por specification.
     *
     * @param filtroActividades the filtro actividades
     * @return the specification
     */
    public static Specification<Actividad> filtrarPor(FiltroActividades filtroActividades) {

        LocalDateTime fechaActual = LocalDateTime.now();

        return Specification
                .where(esTipo(filtroActividades.tipoId()))
                .and(tieneFechaMayorQueInicio(filtroActividades.fechaInicio()))
                .and(tieneFechaMenorQueFin(filtroActividades.fechaFin()))
                .and(tieneMenosAsistentesConfirmadosQueMaximos())
                .and(tieneLocalizacionNombre(filtroActividades.localizacionNombre()))
                .and(noHaTerminado(fechaActual));
    }

    /**
     * Crea una especificación que filtra las actividades por tipo.
     *
     * @param tipoId El ID del tipo de actividad a filtrar. Si es {@code null}, no se aplica filtro.
     * @return Una especificación que filtra las actividades por tipo.
     */
    private static Specification<Actividad> esTipo(Integer tipoId) {
        return (root, query, cb) -> tipoId == null
                ? cb.conjunction()
                : cb.equal(root.get("tipo").get("id"), tipoId);
    }

    /**
     * Crea una especificación que filtra las actividades cuya fecha de inicio es mayor o igual
     * a la fecha de inicio proporcionada.
     *
     * @param fechaInicio La fecha de inicio a comparar. Si es {@code null}, no se aplica filtro.
     * @return Una especificación que filtra las actividades por fecha de inicio.
     */
    private static Specification<Actividad> tieneFechaMayorQueInicio(LocalDateTime fechaInicio) {
        return (root, query, cb) -> fechaInicio == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fechaInicio"), fechaInicio);
    }

    /**
     * Crea una especificación que filtra las actividades cuya fecha de fin es menor o igual
     * a la fecha de fin proporcionada.
     *
     * @param fechaFin La fecha de fin a comparar. Si es {@code null}, no se aplica filtro.
     * @return Una especificación que filtra las actividades por fecha de fin.
     */
    private static Specification<Actividad> tieneFechaMenorQueFin(LocalDateTime fechaFin) {
        return (root, query, cb) -> fechaFin == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("fechaFin"), fechaFin);
    }

    /**
     * Crea una especificación que filtra las actividades cuyas asistentes confirmados
     * sean menores a los máximos asistentes.
     *
     * @return Una especificación que filtra las actividades por asistentes confirmados.
     */
    private static Specification<Actividad> tieneMenosAsistentesConfirmadosQueMaximos() {
        return (root, query, cb) -> cb.lessThan(root.get("asistentesConfirmados"), root.get("maximosAsistentes"));
    }

    /**
     * Crea una especificación que filtra las actividades por el nombre de la localización.
     *
     * @param localizacionNombre El nombre de la localización a filtrar. Si es {@code null} o vacío, no se aplica filtro.
     * @return Una especificación que filtra las actividades por el nombre de la localización.
     */
    private static Specification<Actividad> tieneLocalizacionNombre(String localizacionNombre) {
        return (root, query, cb) -> localizacionNombre == null || localizacionNombre.isEmpty()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("localizacion").get("nombre")), "%" + localizacionNombre.toLowerCase() + "%");
    }

    /**
     * Crea una especificación que filtra las actividades cuya fecha de fin es mayor que la fecha actual.
     *
     * @param fechaActual La fecha y hora actuales.
     * @return Una especificación que filtra las actividades que aún no han terminado.
     */
    private static Specification<Actividad> noHaTerminado(LocalDateTime fechaActual) {
        return (root, query, cb) -> cb.greaterThan(root.get("fechaFin"), fechaActual);
    }

}
