package com.eoi.grupo5.configuration;

import com.eoi.grupo5.modelos.DetallesUsuario;
import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.modelos.Role;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoDetallesUsuario;
import com.eoi.grupo5.repos.RepoRole;
import com.eoi.grupo5.repos.RepoUsuario;
import com.eoi.grupo5.servicios.ServicioUsuario;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final RepoUsuario repoUsuario;
    private final RepoDetallesUsuario repoDetallesUsuario;
    private final RepoRole repoRole;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ApplicationStartup(RepoUsuario repoUsuario, BCryptPasswordEncoder bCryptPasswordEncoder, RepoDetallesUsuario repoDetallesUsuario, RepoRole repoRole) {
        this.repoUsuario = repoUsuario;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.repoDetallesUsuario = repoDetallesUsuario;
        this.repoRole = repoRole;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        Usuario usuario = new Usuario();
        DetallesUsuario detalles = new DetallesUsuario("admin@gmail.com");
        Role rol = new Role();
        rol.setRoleName("admin");

        usuario.setNombreUsuario("admin");
        usuario.setPassword(bCryptPasswordEncoder.encode("admin"));
        usuario.setDetalles(detalles);
        usuario.getRoles().add(rol);

        rol.getUsuarios().add(usuario);
        detalles.setUsu(usuario);
        repoRole.save(rol);
        repoDetallesUsuario.save(detalles);
        repoUsuario.save(usuario);
    }
}
