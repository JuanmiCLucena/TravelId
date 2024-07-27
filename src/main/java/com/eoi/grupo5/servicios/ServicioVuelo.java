package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.repos.RepoVuelo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicioVuelo extends AbstractBusinessServiceSoloEnt<Vuelo, Integer, RepoVuelo>{
    protected ServicioVuelo(RepoVuelo repoVuelo) {
        super(repoVuelo);
    }

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

    public PaginaRespuestaVuelos<Vuelo> obtenerVuelosDisponiblesPaginados(int page, int size, LocalDateTime fechaActual) {
        Pageable pageable = PageRequest.of(page, size);

        // Obtener todos los vuelos disponibles
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
