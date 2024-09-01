package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Pais;
import com.eoi.grupo5.repos.RepoPais;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con los países.
 * Este servicio extiende de {@link AbstractBusinessServiceSoloEnt} para proporcionar
 * funcionalidades CRUD específicas para la entidad {@link Pais}.
 */
@Service
public class ServicioPais extends AbstractBusinessServiceSoloEnt<Pais, Integer, RepoPais> {

    /**
     * Constructor del servicio de países.
     *
     * @param repoPais el repositorio de países que se utilizará para las operaciones de base de datos.
     */
    protected ServicioPais(RepoPais repoPais) {
        super(repoPais);
    }
}
