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

}
