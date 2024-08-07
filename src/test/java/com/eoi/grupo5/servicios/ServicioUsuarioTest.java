package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.paginacion.PaginaRespuestaLocalizaciones;
import com.eoi.grupo5.paginacion.PaginaRespuestaUsuarios;
import com.eoi.grupo5.repos.RepoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ServicioUsuarioTest {

    @Mock
    private RepoUsuario repoUsuario;

    @InjectMocks
    private ServicioUsuario servicioUsuario;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioUsuario = new ServicioUsuario(repoUsuario);
    }

    @Test
    void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Usuario usuario = new Usuario();
        Page<Usuario> usuarioPage = new PageImpl<>(Collections.singletonList(usuario), pageable, 1);

        when(repoUsuario.findAll(pageable)).thenReturn(usuarioPage);

        PaginaRespuestaUsuarios<Usuario> respuesta = servicioUsuario.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(usuario, respuesta.getContent().getFirst());
    }
}