package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Pago;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.paginacion.PaginaRespuestaPagos;
import com.eoi.grupo5.paginacion.PaginaRespuestaPrecios;
import com.eoi.grupo5.repos.RepoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServicioPago extends AbstractBusinessServiceSoloEnt<Pago, Integer, RepoPago> {

    protected ServicioPago(RepoPago repoPago) {
        super(repoPago);
    }

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
