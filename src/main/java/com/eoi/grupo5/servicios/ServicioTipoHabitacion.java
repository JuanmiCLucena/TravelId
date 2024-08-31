package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.TipoHabitacion;
import com.eoi.grupo5.repos.RepoTipoHabitacion;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con el tipo de habitación.
 * Este servicio extiende de {@link AbstractBusinessServiceSoloEnt} para proporcionar
 * funcionalidades CRUD específicas para la entidad {@link TipoHabitacion}.
 */
@Service
public class ServicioTipoHabitacion extends AbstractBusinessServiceSoloEnt<TipoHabitacion, Integer, RepoTipoHabitacion> {

    /**
     * Constructor del servicio de tipo de habitación.
     *
     * @param repoTipoHabitacion el repositorio de tipo de habitación.
     */
    protected ServicioTipoHabitacion(RepoTipoHabitacion repoTipoHabitacion) {
        super(repoTipoHabitacion);
    }
}
