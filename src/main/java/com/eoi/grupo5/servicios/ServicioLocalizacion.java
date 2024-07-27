package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import com.eoi.grupo5.paginacion.PaginaRespuestaLocalizaciones;
import com.eoi.grupo5.repos.RepoLocalizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServicioLocalizacion  extends AbstractBusinessServiceSoloEnt <Localizacion,Integer, RepoLocalizacion>{
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoLocalizacion the repo
     */
    protected ServicioLocalizacion(RepoLocalizacion repoLocalizacion) {
        super(repoLocalizacion);
    }

    public PaginaRespuestaLocalizaciones<Localizacion> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Localizacion> localizacionPage = getRepo().findAll(pageable);

        PaginaRespuestaLocalizaciones<Localizacion> respuesta = new PaginaRespuestaLocalizaciones<>();
        respuesta.setContent(localizacionPage.getContent());
        respuesta.setSize(localizacionPage.getSize());
        respuesta.setTotalSize(localizacionPage.getTotalElements());
        respuesta.setPage(localizacionPage.getNumber());
        respuesta.setTotalPages(localizacionPage.getTotalPages());

        return respuesta;
    }

}
