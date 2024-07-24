package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.HabitacionReservada;
import com.eoi.grupo5.modelos.HabitacionReservadaId;
import com.eoi.grupo5.repos.RepoHabitacionReservada;
import org.springframework.stereotype.Service;

@Service
public class ServicioHabitacionReservada extends AbstractBusinessServiceSoloEnt<HabitacionReservada, HabitacionReservadaId, RepoHabitacionReservada>{
    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repoHabitacionReservada the repo
     */
    protected ServicioHabitacionReservada(RepoHabitacionReservada repoHabitacionReservada) {
        super(repoHabitacionReservada);
    }
}
