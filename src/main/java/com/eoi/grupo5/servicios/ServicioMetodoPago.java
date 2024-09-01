package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.MetodoPago;
import com.eoi.grupo5.repos.RepoMetodoPago;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con los métodos de pago.
 * Proporciona funcionalidades de CRUD a través de la herencia de {@link AbstractBusinessServiceSoloEnt}.
 */
@Service
public class ServicioMetodoPago extends AbstractBusinessServiceSoloEnt<MetodoPago, Integer, RepoMetodoPago> {

    /**
     * Constructor del servicio de métodos de pago.
     *
     * @param repoMetodoPago el repositorio de métodos de pago.
     */
    protected ServicioMetodoPago(RepoMetodoPago repoMetodoPago) {
        super(repoMetodoPago);
    }
}
