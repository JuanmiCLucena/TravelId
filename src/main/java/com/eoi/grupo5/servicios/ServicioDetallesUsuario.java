package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.DetallesUsuario;
import com.eoi.grupo5.repos.RepoDetallesUsuario;
import org.springframework.stereotype.Service;

@Service
public class ServicioDetallesUsuario extends AbstractBusinessServiceSoloEnt<DetallesUsuario,Integer, RepoDetallesUsuario> {
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoDetallesUsuario the repo
     */
    protected ServicioDetallesUsuario(RepoDetallesUsuario repoDetallesUsuario) {
        super(repoDetallesUsuario);
    }
}
