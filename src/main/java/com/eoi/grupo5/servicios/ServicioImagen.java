package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.repos.RepoImagen;
import org.springframework.stereotype.Service;

@Service
public class ServicioImagen extends AbstractBusinessServiceSoloEnt<Imagen, Integer, RepoImagen> {
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoImagen the repo
     */
    protected ServicioImagen(RepoImagen repoImagen) {
        super(repoImagen);
    }
}
