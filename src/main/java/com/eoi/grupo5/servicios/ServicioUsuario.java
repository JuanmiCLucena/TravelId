package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.paginacion.PaginaRespuestaUsuarios;
import com.eoi.grupo5.repos.RepoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para gestionar las operaciones relacionadas con los usuarios.
 * Este servicio extiende de {@link AbstractBusinessServiceSoloEnt} para proporcionar
 * funcionalidades CRUD específicas para la entidad {@link Usuario}.
 */
@Service
public class ServicioUsuario extends AbstractBusinessServiceSoloEnt<Usuario, Integer, RepoUsuario> {

    private final RepoUsuario repoUsuario;

    /**
     * Constructor del servicio de usuario.
     *
     * @param repoUsuario el repositorio de usuarios.
     */
    protected ServicioUsuario(RepoUsuario repoUsuario) {
        super(repoUsuario);
        this.repoUsuario = repoUsuario;
    }

    /**
     * Busca las entidades de usuarios paginadas.
     *
     * @param page el número de página (0 basado).
     * @param size el tamaño de la página.
     * @return una {@link PaginaRespuestaUsuarios} que contiene la lista de usuarios, tamaño de la página,
     *         tamaño total y números de página.
     */
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

    /**
     * Encuentra un usuario por su nombre de usuario.
     *
     * @param nombreUsuario el nombre de usuario a buscar.
     * @return un {@link Optional} que contiene el usuario encontrado si existe, de lo contrario vacío.
     */
    public Optional<Usuario> encuentraPorNombreUsuario(String nombreUsuario) {
        return repoUsuario.findByNombreUsuario(nombreUsuario);
    }

    /**
     * Guarda el usuario en el repositorio.
     *
     * @param usuario el usuario a guardar.
     * @return el usuario guardado.
     */
    public Usuario guardar(Usuario usuario) {
        return repoUsuario.save(usuario);
    }
}
