package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.paginacion.PaginaRespuestaPrecios;
import com.eoi.grupo5.repos.RepoPrecio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con los precios.
 * Este servicio extiende de {@link AbstractBusinessServiceSoloEnt} para proporcionar
 * funcionalidades CRUD específicas para la entidad {@link Precio}.
 */
@Service
public class ServicioPrecio extends AbstractBusinessServiceSoloEnt<Precio, Integer, RepoPrecio> {

    /**
     * Constructor del servicio de precios.
     *
     * @param repoPrecio el repositorio de precios que se utilizará para las operaciones de base de datos.
     */
    protected ServicioPrecio(RepoPrecio repoPrecio) {
        super(repoPrecio);
    }

    /**
     * Busca las entidades de precios paginadas.
     *
     * @param page el número de página a recuperar.
     * @param size el tamaño de la página a recuperar.
     * @return una instancia de {@link PaginaRespuestaPrecios} que contiene la página solicitada de precios.
     */
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
