package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Asiento;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaAsientos;
import com.eoi.grupo5.repos.RepoActividad;
import com.eoi.grupo5.repos.RepoAsiento;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ServicioAsientoTest {

    @Mock
    private RepoAsiento repoAsiento;

    private ServicioAsiento servicioAsiento;

    private Asiento asiento;
    private Precio precio;
    private Vuelo vuelo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioAsiento = new ServicioAsiento(repoAsiento);

        asiento = new Asiento();
        asiento.setId(1);

        precio = new Precio();
        precio.setValor(500.00);


        vuelo = new Vuelo();
        vuelo.getAsientos().add(asiento);
    }

    @Test
    void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Asiento asiento = new Asiento();
        Page<Asiento> asientoPage = new PageImpl<>(Collections.singletonList(asiento), pageable, 1);

        when(repoAsiento.findAll(pageable)).thenReturn(asientoPage);

        PaginaRespuestaAsientos<Asiento> respuesta = servicioAsiento.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(asiento, respuesta.getContent().getFirst());
    }

    @Test
    void testGetPrecioActual() {
        LocalDateTime fechaActual = LocalDateTime.now();

        precio.setFechaInicio(fechaActual.minusDays(1));
        precio.setFechaFin(fechaActual.plusDays(1));

        asiento.getPrecio().add(precio);

        Precio precioActual = servicioAsiento.getPrecioActual(asiento, fechaActual);

        assertNotNull(precioActual);
        assertEquals(precio.getValor(), precioActual.getValor());
    }

    @Test
    void testObtenerPreciosActualesAsientosVuelo() {
        LocalDateTime now = LocalDateTime.now();

        Asiento asiento1 = new Asiento();
        asiento1.setId(1);
        Asiento asiento2 = new Asiento();
        asiento2.setId(2);

        Precio precio1 = new Precio();
        precio1.setValor(100.0);
        precio1.setFechaInicio(now.minusDays(1));
        precio1.setFechaFin(now.plusDays(1));

        Precio precio2 = new Precio();
        precio2.setValor(200.0);
        precio2.setFechaInicio(now.plusMonths(1));
        precio2.setFechaFin(now.plusMonths(2));

        Precio precio3 = new Precio();
        precio3.setValor(300.0);
        precio3.setFechaInicio(now.minusDays(1));
        precio3.setFechaFin(now.plusDays(1));

        Precio precio4 = new Precio();
        precio4.setValor(400.0);
        precio4.setFechaInicio(now.plusMonths(1));
        precio4.setFechaFin(now.plusMonths(2));

        asiento1.getPrecio().add(precio1);
        asiento1.getPrecio().add(precio2);
        asiento2.getPrecio().add(precio3);
        asiento2.getPrecio().add(precio4);

        Vuelo vuelo1 = new Vuelo();
        vuelo1.getAsientos().add(asiento1);
        vuelo1.getAsientos().add(asiento2);


        Map<Integer, Double> preciosActuales = servicioAsiento.obtenerPreciosActualesAsientosVuelo(vuelo1);

        assertNotNull(preciosActuales);
        assertEquals(precio1.getValor(), preciosActuales.get(asiento1.getId()));
        assertEquals(precio3.getValor(), preciosActuales.get(asiento2.getId()));
    }
}