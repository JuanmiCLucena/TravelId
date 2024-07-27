package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import com.eoi.grupo5.paginacion.PaginaRespuestaPrecios;
import com.eoi.grupo5.repos.RepoPrecio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServicioPrecio extends AbstractBusinessServiceSoloEnt<Precio, Integer, RepoPrecio>{
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoPrecio the repo
     */
    protected ServicioPrecio(RepoPrecio repoPrecio) {
        super(repoPrecio);
    }

    public PaginaRespuestaPrecios<Precio> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Precio> precioPage = getRepo().findAll(pageable);

        PaginaRespuestaPrecios<Precio> respuesta = new PaginaRespuestaPrecios<>();
        respuesta.setContent(precioPage.getContent());
        respuesta.setSize(precioPage.getSize());
        respuesta.setTotalSize(precioPage.getTotalElements());
        respuesta.setPage(precioPage.getNumber());
        respuesta.setTotalPages(precioPage.getTotalPages());

        return respuesta;
    }

}
