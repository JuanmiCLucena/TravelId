package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.MetodoPago;
import com.eoi.grupo5.repos.RepoMetodoPago;
import com.eoi.grupo5.repos.RepoPago;
import org.springframework.stereotype.Service;

@Service
public class ServicioMetodoPago extends AbstractBusinessServiceSoloEnt<MetodoPago, Integer, RepoMetodoPago> {

    protected ServicioMetodoPago(RepoMetodoPago repoMetodoPago) {
        super(repoMetodoPago);
    }
}
