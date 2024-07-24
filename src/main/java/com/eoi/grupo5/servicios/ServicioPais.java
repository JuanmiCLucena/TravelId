package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Pais;
import com.eoi.grupo5.repos.RepoPais;
import org.springframework.stereotype.Service;

@Service
public class ServicioPais extends AbstractBusinessServiceSoloEnt<Pais,Integer, RepoPais> {
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoPais the repo
     */
    protected ServicioPais(RepoPais repoPais) {
        super(repoPais);
    }
}
