package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Pago;
import com.eoi.grupo5.paginacion.PaginaRespuestaPagos;
import com.eoi.grupo5.repos.RepoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con los pagos.
 * Proporciona funcionalidades de CRUD a través de la herencia de {@link AbstractBusinessServiceSoloEnt}.
 */
@Service
public class ServicioPago extends AbstractBusinessServiceSoloEnt<Pago, Integer, RepoPago> {

    /**
     * Constructor del servicio de pagos.
     *
     * @param repoPago el repositorio de pagos.
     */
    protected ServicioPago(RepoPago repoPago) {
        super(repoPago);
    }

    /**
     * Busca y devuelve una página de entidades de pago.
     *
     * Este método permite obtener una lista paginada de pagos desde el repositorio,
     * facilitando la visualización y manejo de grandes volúmenes de datos.
     *
     * @param page el número de la página a recuperar (empezando desde 0).
     * @param size el número de elementos por página.
     * @return una instancia de {@link PaginaRespuestaPagos} que contiene la lista de pagos
     *         y la información de paginación (tamaño de la página, número total de elementos, etc.).
     */
    public PaginaRespuestaPagos<Pago> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pago> pagoPage = getRepo().findAll(pageable);

        PaginaRespuestaPagos<Pago> respuesta = new PaginaRespuestaPagos<>();
        respuesta.setContent(pagoPage.getContent());
        respuesta.setSize(pagoPage.getSize());
        respuesta.setTotalSize(pagoPage.getTotalElements());
        respuesta.setPage(pagoPage.getNumber());
        respuesta.setTotalPages(pagoPage.getTotalPages());

        return respuesta;
    }
}
