package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.repos.RepoVuelo;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ServicioVueloTest {

    @Mock
    private RepoVuelo repoVuelo;

    @InjectMocks
    private ServicioVuelo servicioVuelo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioVuelo = new ServicioVuelo(repoVuelo);
    }

    @Test
    void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Vuelo vuelo = new Vuelo();
        Page<Vuelo> vueloPage = new PageImpl<>(Collections.singletonList(vuelo), pageable, 1);

        when(repoVuelo.findAll((Specification<Vuelo>) null, pageable)).thenReturn(vueloPage);

        PaginaRespuestaVuelos<Vuelo> respuesta = servicioVuelo.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(vuelo, respuesta.getContent().getFirst());
    }

    @Test
    void testObtenerVuelosDisponiblesPaginados() {
// Crear datos de prueba
        LocalDateTime fechaActual = LocalDateTime.now();

        Vuelo vuelo1 = new Vuelo();
        vuelo1.setId(1);
        vuelo1.setNombre("Vuelo 1");

        Vuelo vuelo2 = new Vuelo();
        vuelo2.setId(2);
        vuelo2.setNombre("Vuelo 2");

        Vuelo vuelo3 = new Vuelo();
        vuelo3.setId(3);
        vuelo3.setNombre("Vuelo 3");

        Vuelo vuelo4 = new Vuelo();
        vuelo4.setId(4);
        vuelo4.setNombre("Vuelo 4");

        List<Vuelo> vuelosDisponibles = Arrays.asList(vuelo1, vuelo2, vuelo3, vuelo4);

        // Configurar el comportamiento del mock
        when(repoVuelo.findVuelosDisponibles(fechaActual)).thenReturn(vuelosDisponibles);

        // Ejecutar el método a probar
        PaginaRespuestaVuelos<Vuelo> resultado = servicioVuelo.obtenerVuelosDisponiblesPaginados(0, 2, fechaActual);

        // Verificar los resultados
        assertEquals(2, resultado.getContent().size());
        assertEquals(vuelo1, resultado.getContent().get(0));
        assertEquals(vuelo2, resultado.getContent().get(1));
        assertEquals(4, resultado.getTotalSize());
        assertEquals(2, resultado.getTotalPages());
        assertEquals(2, resultado.getSize());
        assertEquals(0, resultado.getPage());

        // Ejecutar el método a probar para la segunda página
        PaginaRespuestaVuelos<Vuelo> resultadoPagina2 = servicioVuelo.obtenerVuelosDisponiblesPaginados(1, 2, fechaActual);

        // Verificar los resultados para la segunda página
        assertEquals(2, resultadoPagina2.getContent().size());
        assertEquals(vuelo3, resultadoPagina2.getContent().get(0));
        assertEquals(vuelo4, resultadoPagina2.getContent().get(1));
        assertEquals(4, resultadoPagina2.getTotalSize());
        assertEquals(2, resultadoPagina2.getTotalPages());
        assertEquals(2, resultadoPagina2.getSize());
        assertEquals(1, resultadoPagina2.getPage());
    }
}