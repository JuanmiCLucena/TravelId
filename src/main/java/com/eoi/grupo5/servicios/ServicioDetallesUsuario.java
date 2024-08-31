package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.DetallesUsuario;
import com.eoi.grupo5.repos.RepoDetallesUsuario;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar las operaciones relacionadas con los detalles de los usuarios.
 * Extiende la clase {@link AbstractBusinessServiceSoloEnt} para proporcionar funcionalidades estándar de CRUD.
 */
@Service
public class ServicioDetallesUsuario extends AbstractBusinessServiceSoloEnt<DetallesUsuario, Integer, RepoDetallesUsuario> {

    /**
     * Constructor del servicio que inyecta el repositorio de detalles de usuario.
     *
     * @param repoDetallesUsuario Repositorio que gestiona las operaciones de persistencia para las entidades {@link DetallesUsuario}.
     */
    protected ServicioDetallesUsuario(RepoDetallesUsuario repoDetallesUsuario) {
        super(repoDetallesUsuario);
    }
}
