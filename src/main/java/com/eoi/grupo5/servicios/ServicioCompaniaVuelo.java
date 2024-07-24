package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.CompaniaVuelo;
import com.eoi.grupo5.repos.RepoCompaniaVuelo;
import org.springframework.stereotype.Service;

@Service
public class ServicioCompaniaVuelo extends AbstractBusinessServiceSoloEnt<CompaniaVuelo,Integer, RepoCompaniaVuelo> {
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoCompaniaVuelo the repo
     */
    protected ServicioCompaniaVuelo(RepoCompaniaVuelo repoCompaniaVuelo) {
        super(repoCompaniaVuelo);
    }
}
