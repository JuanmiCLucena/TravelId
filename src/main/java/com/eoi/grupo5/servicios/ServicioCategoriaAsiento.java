package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.CategoriaAsiento;
import com.eoi.grupo5.repos.RepoCategoriaAsiento;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con las categorías de asientos.
 * Extiende la clase {@link AbstractBusinessServiceSoloEnt} para proporcionar funcionalidades adicionales.
 */
@Service
public class ServicioCategoriaAsiento extends AbstractBusinessServiceSoloEnt<CategoriaAsiento, Integer, RepoCategoriaAsiento> {

    /**
     * Constructor del servicio que inyecta el repositorio de categorías de asientos.
     *
     * @param repoCategoriaAsiento Repositorio que gestiona las operaciones de persistencia para las categorías de asientos.
     */
    protected ServicioCategoriaAsiento(RepoCategoriaAsiento repoCategoriaAsiento) {
        super(repoCategoriaAsiento);
    }
}
