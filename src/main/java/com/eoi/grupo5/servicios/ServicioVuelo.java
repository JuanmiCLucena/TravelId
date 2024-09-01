package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.repos.RepoVuelo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar operaciones relacionadas con los vuelos.
 * Este servicio extiende de {@link AbstractBusinessServiceSoloEnt} para proporcionar
 * funcionalidades CRUD específicas para la entidad {@link Vuelo}.
 */
@Service
public class ServicioVuelo extends AbstractBusinessServiceSoloEnt<Vuelo, Integer, RepoVuelo> {

    /**
     * Constructor del servicio de vuelo.
     *
     * @param repoVuelo el repositorio de vuelos.
     */
    protected ServicioVuelo(RepoVuelo repoVuelo) {
        super(repoVuelo);
    }

    /**
     * Busca todas las entidades de vuelos paginadas.
     *
     * @param page el número de página (0 basado).
     * @param size el tamaño de la página.
     * @return una {@link PaginaRespuestaVuelos} que contiene la lista de vuelos, tamaño de la página,
     *         tamaño total y números de página.
     */
    public PaginaRespuestaVuelos<Vuelo> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vuelo> vueloPage = getRepo().findAll((Specification<Vuelo>) null, pageable);

        PaginaRespuestaVuelos<Vuelo> respuesta = new PaginaRespuestaVuelos<>();
        respuesta.setContent(vueloPage.getContent());
        respuesta.setSize(vueloPage.getSize());
        respuesta.setTotalSize(vueloPage.getTotalElements());
        respuesta.setPage(vueloPage.getNumber());
        respuesta.setTotalPages(vueloPage.getTotalPages());

        return respuesta;
    }

    /**
     * Obtiene los vuelos disponibles paginados a partir de una fecha actual.
     *
     * @param page el número de página (0 basado).
     * @param size el tamaño de la página.
     * @param fechaActual la fecha actual utilizada para filtrar los vuelos disponibles.
     * @return una {@link PaginaRespuestaVuelos} que contiene la lista de vuelos disponibles, tamaño de la página,
     *         tamaño total y números de página.
     */
    public PaginaRespuestaVuelos<Vuelo> obtenerVuelosDisponiblesPaginados(int page, int size, LocalDateTime fechaActual) {
        Pageable pageable = PageRequest.of(page, size);

        // Obtener todos los vuelos disponibles según la fecha actual
        List<Vuelo> vuelosDisponibles = getRepo().findVuelosDisponibles(fechaActual);

        // Calcular el inicio y fin de la sublista para la página actual
        int start = Math.min((int) pageable.getOffset(), vuelosDisponibles.size());
        int end = Math.min((start + pageable.getPageSize()), vuelosDisponibles.size());

        // Crear la página usando PaginaRespuestaVuelos
        PaginaRespuestaVuelos<Vuelo> vuelosPage = new PaginaRespuestaVuelos<>();
        vuelosPage.setContent(vuelosDisponibles.subList(start, end));
        vuelosPage.setSize(pageable.getPageSize());
        vuelosPage.setTotalSize(vuelosDisponibles.size());
        vuelosPage.setPage(pageable.getPageNumber());
        vuelosPage.setTotalPages((int) Math.ceil((double) vuelosDisponibles.size() / pageable.getPageSize()));

        return vuelosPage;
    }
}
