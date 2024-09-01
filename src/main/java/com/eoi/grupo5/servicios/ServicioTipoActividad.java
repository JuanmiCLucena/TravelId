package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.TipoActividad;
import com.eoi.grupo5.repos.RepoTipoActividad;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con el tipo de actividad.
 * Este servicio extiende de {@link AbstractBusinessServiceSoloEnt} para proporcionar
 * funcionalidades CRUD específicas para la entidad {@link TipoActividad}.
 */
@Service
public class ServicioTipoActividad extends AbstractBusinessServiceSoloEnt<TipoActividad, Integer, RepoTipoActividad> {

    /**
     * Constructor del servicio de tipo de actividad.
     *
     * @param repoTipoActividad el repositorio de tipo de actividad.
     */
    protected ServicioTipoActividad(RepoTipoActividad repoTipoActividad) {
        super(repoTipoActividad);
    }
}
