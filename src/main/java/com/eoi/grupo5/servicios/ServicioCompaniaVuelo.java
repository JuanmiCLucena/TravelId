package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.CompaniaVuelo;
import com.eoi.grupo5.repos.RepoCompaniaVuelo;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con las compañías de vuelo.
 * Extiende la clase {@link AbstractBusinessServiceSoloEnt} para proporcionar funcionalidades estándar de CRUD.
 */
@Service
public class ServicioCompaniaVuelo extends AbstractBusinessServiceSoloEnt<CompaniaVuelo, Integer, RepoCompaniaVuelo> {

    /**
     * Constructor del servicio que inyecta el repositorio de compañías de vuelo.
     *
     * @param repoCompaniaVuelo Repositorio que gestiona las operaciones de persistencia para las entidades {@link CompaniaVuelo}.
     */
    protected ServicioCompaniaVuelo(RepoCompaniaVuelo repoCompaniaVuelo) {
        super(repoCompaniaVuelo);
    }
}
