package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaLocalizaciones;
import com.eoi.grupo5.repos.RepoActividad;
import com.eoi.grupo5.repos.RepoLocalizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ServicioLocalizacionTest {

    @Mock
    private RepoLocalizacion repoLocalizacion;

    private ServicioLocalizacion servicioLocalizacion;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioLocalizacion = new ServicioLocalizacion(repoLocalizacion);
    }

    @Test
    void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Localizacion localizacion = new Localizacion();
        Page<Localizacion> localizacionPage = new PageImpl<>(Collections.singletonList(localizacion), pageable, 1);

        when(repoLocalizacion.findAll(pageable)).thenReturn(localizacionPage);

        PaginaRespuestaLocalizaciones<Localizacion> respuesta = servicioLocalizacion.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(localizacion, respuesta.getContent().getFirst());
    }
}