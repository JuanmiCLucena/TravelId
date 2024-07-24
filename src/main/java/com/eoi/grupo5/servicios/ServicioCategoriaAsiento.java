package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.CategoriaAsiento;
import com.eoi.grupo5.repos.RepoCategoriaAsiento;
import org.springframework.stereotype.Service;

@Service
public class ServicioCategoriaAsiento extends AbstractBusinessServiceSoloEnt<CategoriaAsiento,Integer, RepoCategoriaAsiento>{
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoCategoriaAsiento the repo
     */
    protected ServicioCategoriaAsiento(RepoCategoriaAsiento repoCategoriaAsiento) {
        super(repoCategoriaAsiento);
    }
}
