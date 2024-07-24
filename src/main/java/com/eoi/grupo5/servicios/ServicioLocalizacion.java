package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.repos.RepoLocalizacion;
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
}
