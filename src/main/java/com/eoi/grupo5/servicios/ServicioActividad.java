package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.dtos.ActividadDto;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.repos.RepoActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio para gestionar las operaciones relacionadas con las actividades.
 * Extiende la clase {@link AbstractBusinessServiceSoloEnt} para proporcionar funcionalidad de negocio adicional.
 */
@Service
public class ServicioActividad extends AbstractBusinessServiceSoloEnt<Actividad, Integer, RepoActividad> {

    /**
     * Constructor del servicio que inyecta el repositorio de actividades.
     *
     * @param repoActividad Repositorio de actividades que gestiona las operaciones de persistencia.
     */
    protected ServicioActividad(RepoActividad repoActividad) {
        super(repoActividad);
    }

    /**
     * Obtiene una lista paginada de actividades disponibles en función de la fecha actual.
     *
     * @param page        Número de página a obtener.
     * @param size        Tamaño de la página.
     * @param fechaActual Fecha actual para filtrar las actividades disponibles.
     * @return Una instancia de {@link PaginaRespuestaActividades} que contiene las actividades disponibles en la página solicitada.
     */
    public PaginaRespuestaActividades<Actividad> obtenerActividadesDisponiblesPaginadas(int page, int size, LocalDateTime fechaActual) {
        Pageable pageable = PageRequest.of(page, size);

        // Obtener todas las actividades disponibles
        List<Actividad> actividadesDisponibles = getRepo().findActividadesDisponibles(fechaActual);

        // Calcular el inicio y fin de la sublista para la página actual
        int start = Math.min((int) pageable.getOffset(), actividadesDisponibles.size());
        int end = Math.min((start + pageable.getPageSize()), actividadesDisponibles.size());

        // Crear la página usando PaginaRespuestaActividades
        PaginaRespuestaActividades<Actividad> actividadesPage = new PaginaRespuestaActividades<>();
        actividadesPage.setContent(actividadesDisponibles.subList(start, end));
        actividadesPage.setSize(pageable.getPageSize());
        actividadesPage.setTotalSize(actividadesDisponibles.size());
        actividadesPage.setPage(pageable.getPageNumber());
        actividadesPage.setTotalPages((int) Math.ceil((double) actividadesDisponibles.size() / pageable.getPageSize()));

        return actividadesPage;
    }

    /**
     * Busca todas las actividades de forma paginada.
     *
     * @param page Número de página a obtener.
     * @param size Tamaño de la página.
     * @return Una instancia de {@link PaginaRespuestaActividades} que contiene las actividades en la página solicitada.
     */
    public PaginaRespuestaActividades<Actividad> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Actividad> actividadPage = getRepo().findAll((Specification<Actividad>) null, pageable);

        PaginaRespuestaActividades<Actividad> respuesta = new PaginaRespuestaActividades<>();
        respuesta.setContent(actividadPage.getContent());
        respuesta.setSize(actividadPage.getSize());
        respuesta.setTotalSize(actividadPage.getTotalElements());
        respuesta.setPage(actividadPage.getNumber());
        respuesta.setTotalPages(actividadPage.getTotalPages());

        return respuesta;
    }

    /**
     * Obtiene una lista de actividades en la misma ubicación que la actividad proporcionada.
     *
     * @param actividad Actividad que se utiliza para buscar actividades en la misma ubicación.
     * @return Una lista de actividades en la misma ubicación que la actividad proporcionada.
     */
    public List<Actividad> obtenerActividadesEnTuZona(Actividad actividad) {
        return super.buscarEntidades()
                .stream()
                .filter(a -> a.getLocalizacion().getNombre().equals(actividad.getLocalizacion().getNombre()) && !Objects.equals(a.getId(), actividad.getId()))
                .limit(2)
                .toList();
    }

    /**
     * Obtiene el precio actual de una actividad en función de la fecha actual.
     *
     * @param actividad   Actividad de la cual se desea obtener el precio actual.
     * @param fechaActual Fecha actual utilizada para filtrar los precios.
     * @return El precio actual de la actividad, o null si no se encuentra un precio válido.
     */
    public Precio getPrecioActual(Actividad actividad, LocalDateTime fechaActual) {
        return actividad.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) && (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene un mapa con los precios actuales de una lista de actividades.
     *
     * @param actividades Lista de actividades de las cuales se desean obtener los precios actuales.
     * @return Un mapa que asocia el ID de la actividad con su precio actual. Si no se encuentra un precio válido, el valor será null.
     */
    public Map<Integer, Double> obtenerPreciosActualesActividades(List<Actividad> actividades) {
        LocalDateTime fechaActual = LocalDateTime.now();
        Map<Integer, Double> preciosActuales = new HashMap<>();

        actividades.forEach(actividad -> {
            Precio precioActual = getPrecioActual(actividad, fechaActual);
            if (precioActual != null) {
                preciosActuales.put(actividad.getId(), precioActual.getValor());
            } else {
                preciosActuales.put(actividad.getId(), null);
            }
        });

        return preciosActuales;
    }

    /**
     * Obtiene el precio actual de una actividad representada por un DTO en función de la fecha actual.
     *
     * @param actividad   DTO de la actividad de la cual se desea obtener el precio actual.
     * @param fechaActual Fecha actual utilizada para filtrar los precios.
     * @return El precio actual de la actividad, o null si no se encuentra un precio válido.
     */
    public Precio getPrecioActual(ActividadDto actividad, LocalDateTime fechaActual) {
        return actividad.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) && (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene un mapa con los precios actuales de una lista paginada de actividades representadas por DTOs.
     *
     * @param actividades Página de actividades DTO de las cuales se desean obtener los precios actuales.
     * @return Un mapa que asocia el ID de la actividad con su precio actual. Si no se encuentra un precio válido, el valor será null.
     */
    public Map<Integer, Double> obtenerPreciosActualesActividades(PaginaRespuestaActividades<ActividadDto> actividades) {
        LocalDateTime fechaActual = LocalDateTime.now();
        Map<Integer, Double> preciosActuales = new HashMap<>();

        actividades.forEach(actividad -> {
            Precio precioActual = getPrecioActual(actividad, fechaActual);
            if (precioActual != null) {
                preciosActuales.put(actividad.getId(), precioActual.getValor());
            } else {
                preciosActuales.put(actividad.getId(), null);
            }
        });

        return preciosActuales;
    }
}
