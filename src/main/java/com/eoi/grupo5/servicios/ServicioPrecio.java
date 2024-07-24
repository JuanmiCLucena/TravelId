package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.repos.RepoPrecio;
import org.springframework.stereotype.Service;

@Service
public class ServicioPrecio extends AbstractBusinessServiceSoloEnt<Precio, Integer, RepoPrecio>{
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoPrecio the repo
     */
    protected ServicioPrecio(RepoPrecio repoPrecio) {
        super(repoPrecio);
    }
}
