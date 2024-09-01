package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoUsuario;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Servicio personalizado para la autenticación de usuarios que implementa la interfaz {@link UserDetailsService}.
 * Proporciona la lógica necesaria para cargar detalles del usuario a partir del nombre de usuario,
 * integrando la capa de persistencia con la autenticación de Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final RepoUsuario repoUsuario;
    private final MessageSource messageSource;

    /**
     * Constructor que inyecta las dependencias requeridas.
     *
     * @param repoUsuario   Repositorio para gestionar operaciones relacionadas con la entidad {@link Usuario}.
     * @param messageSource Servicio para la internacionalización de mensajes de error.
     */
    public CustomUserDetailsService(RepoUsuario repoUsuario, MessageSource messageSource) {
        this.repoUsuario = repoUsuario;
        this.messageSource = messageSource;
    }

    /**
     * Carga los detalles del usuario a partir de su nombre de usuario.
     * Si el usuario no se encuentra en la base de datos, lanza una excepción {@link UsernameNotFoundException}.
     *
     * @param nombreUsuario Nombre de usuario que se usará para la búsqueda del usuario en el repositorio.
     * @return {@link UserDetails} que contiene la información del usuario autenticado.
     * @throws UsernameNotFoundException Si el usuario no se encuentra en la base de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Optional<Usuario> usuario = repoUsuario.findByNombreUsuario(nombreUsuario);
        if (usuario.isPresent()) {
            return usuario.get();
        } else {
            String errorMessage = messageSource.getMessage("user.not.found", null, Locale.getDefault());
            throw new UsernameNotFoundException(errorMessage);
        }
    }
}
