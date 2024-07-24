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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final RepoUsuario repoUsuario;
    private final MessageSource messageSource;

    public CustomUserDetailsService(RepoUsuario repoUsuario, MessageSource messageSource) {
        this.repoUsuario = repoUsuario;
        this.messageSource = messageSource;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {

        Optional<Usuario> usuario = repoUsuario.findByNombreUsuario(nombreUsuario);
        if (usuario.isPresent()){
            return usuario.get();
        } else {
          String errorMessage = messageSource.getMessage("user.not.found", null, Locale.getDefault());
          throw new UsernameNotFoundException(errorMessage);
        }
    }
}
