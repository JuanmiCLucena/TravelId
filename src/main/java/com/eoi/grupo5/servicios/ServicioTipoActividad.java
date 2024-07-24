package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.TipoActividad;
import com.eoi.grupo5.repos.RepoTipoActividad;
import org.springframework.stereotype.Service;

@Service
public class ServicioTipoActividad extends AbstractBusinessServiceSoloEnt<TipoActividad, Integer, RepoTipoActividad>{
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoTipoActividad the repo
     */
    protected ServicioTipoActividad(RepoTipoActividad repoTipoActividad) {
        super(repoTipoActividad);
    }
}
