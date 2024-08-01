package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.paginacion.PaginaRespuestaUsuarios;
import com.eoi.grupo5.repos.RepoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ServicioUsuario extends AbstractBusinessServiceSoloEnt<Usuario, Integer, RepoUsuario>{

    protected ServicioUsuario(RepoUsuario repoUsuario) {
        super(repoUsuario);
    }

    public PaginaRespuestaUsuarios<Usuario> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuarioPage = getRepo().findAll((Specification<Usuario>) null, pageable);

        PaginaRespuestaUsuarios<Usuario> respuesta = new PaginaRespuestaUsuarios<>();
        respuesta.setContent(usuarioPage.getContent());
        respuesta.setSize(usuarioPage.getSize());
        respuesta.setTotalSize(usuarioPage.getTotalElements());
        respuesta.setPage(usuarioPage.getNumber());
        respuesta.setTotalPages(usuarioPage.getTotalPages());

        return respuesta;
    }
}
