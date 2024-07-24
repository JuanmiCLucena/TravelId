package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoUsuario;
import org.springframework.stereotype.Service;

@Service
public class ServicioUsuario extends AbstractBusinessServiceSoloEnt<Usuario, Integer, RepoUsuario>{

    protected ServicioUsuario(RepoUsuario repoUsuario) {
        super(repoUsuario);
    }
}
