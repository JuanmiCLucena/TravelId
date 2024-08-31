package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.repos.RepoImagen;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con las imágenes.
 * Proporciona funcionalidades de CRUD a través de la herencia de {@link AbstractBusinessServiceSoloEnt}.
 */
@Service
public class ServicioImagen extends AbstractBusinessServiceSoloEnt<Imagen, Integer, RepoImagen> {

    /**
     * Constructor del servicio de imágenes.
     *
     * @param repoImagen el repositorio de imágenes.
     */
    protected ServicioImagen(RepoImagen repoImagen) {
        super(repoImagen);
    }
}
