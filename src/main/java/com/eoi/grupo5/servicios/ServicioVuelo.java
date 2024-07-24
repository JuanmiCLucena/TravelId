package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.repos.RepoVuelo;
import org.springframework.stereotype.Service;

@Service
public class ServicioVuelo extends AbstractBusinessServiceSoloEnt<Vuelo, Integer, RepoVuelo>{
    protected ServicioVuelo(RepoVuelo repoVuelo) {
        super(repoVuelo);
    }
}
