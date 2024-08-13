package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.paginacion.PaginaRespuestaUsuarios;
import com.eoi.grupo5.repos.RepoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServicioUsuario extends AbstractBusinessServiceSoloEnt<Usuario, Integer, RepoUsuario>{

    private final RepoUsuario repoUsuario;

    protected ServicioUsuario(RepoUsuario repoUsuario) {
        super(repoUsuario);
        this.repoUsuario = repoUsuario;
    }

    public PaginaRespuestaUsuarios<Usuario> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuarioPage = getRepo().findAll(pageable);

        PaginaRespuestaUsuarios<Usuario> respuesta = new PaginaRespuestaUsuarios<>();
        respuesta.setContent(usuarioPage.getContent());
        respuesta.setSize(usuarioPage.getSize());
        respuesta.setTotalSize(usuarioPage.getTotalElements());
        respuesta.setPage(usuarioPage.getNumber());
        respuesta.setTotalPages(usuarioPage.getTotalPages());

        return respuesta;
    }

    public Optional<Usuario> encuentraPorNombreUsuario(String nombreUsuario) {
        return repoUsuario.findByNombreUsuario(nombreUsuario);
    }

    public Usuario guardar(Usuario usuario) {
        repoUsuario.save(usuario);
        return usuario;
    }
}
