package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.TipoHabitacion;
import com.eoi.grupo5.repos.RepoTipoHabitacion;
import org.springframework.stereotype.Service;

@Service
public class ServicioTipoHabitacion extends AbstractBusinessServiceSoloEnt<TipoHabitacion, Integer, RepoTipoHabitacion>{
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoTipoHabitacion the repo
     */
    protected ServicioTipoHabitacion(RepoTipoHabitacion repoTipoHabitacion) {
        super(repoTipoHabitacion);
    }
}
