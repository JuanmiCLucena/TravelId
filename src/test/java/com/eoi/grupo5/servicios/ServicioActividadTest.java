package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import com.eoi.grupo5.repos.RepoActividad;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


public class ServicioActividadTest {

    @Mock
    private RepoActividad repoActividad;


    private ServicioActividad servicioActividad;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioActividad = new ServicioActividad(repoActividad);
    }

    @Test
    void testObtenerActividadesDisponiblesPaginadas() {
        // Crear datos de prueba
        LocalDateTime fechaActual = LocalDateTime.now();

        Actividad actividad1 = new Actividad();
        actividad1.setId(1);
        actividad1.setNombre("Actividad 1");

        Actividad actividad2 = new Actividad();
        actividad2.setId(2);
        actividad2.setNombre("Actividad 2");

        Actividad actividad3 = new Actividad();
        actividad3.setId(3);
        actividad3.setNombre("Actividad 3");

        Actividad actividad4 = new Actividad();
        actividad4.setId(4);
        actividad4.setNombre("Actividad 4");

        List<Actividad> actividadesDisponibles = Arrays.asList(actividad1, actividad2, actividad3, actividad4);

        // Configurar el comportamiento del mock
        when(repoActividad.findActividadesDisponibles(fechaActual)).thenReturn(actividadesDisponibles);

        // Ejecutar el método a probar
        PaginaRespuestaActividades<Actividad> resultado = servicioActividad.obtenerActividadesDisponiblesPaginadas(0, 2, fechaActual);

        // Verificar los resultados
        assertEquals(2, resultado.getContent().size());
        assertEquals(actividad1, resultado.getContent().get(0));
        assertEquals(actividad2, resultado.getContent().get(1));
        assertEquals(4, resultado.getTotalSize());
        assertEquals(2, resultado.getTotalPages());
        assertEquals(2, resultado.getSize());
        assertEquals(0, resultado.getPage());

        // Ejecutar el método a probar para la segunda página
        PaginaRespuestaActividades<Actividad> resultadoPagina2 = servicioActividad.obtenerActividadesDisponiblesPaginadas(1, 2, fechaActual);

        // Verificar los resultados para la segunda página
        assertEquals(2, resultadoPagina2.getContent().size());
        assertEquals(actividad3, resultadoPagina2.getContent().get(0));
        assertEquals(actividad4, resultadoPagina2.getContent().get(1));
        assertEquals(4, resultadoPagina2.getTotalSize());
        assertEquals(2, resultadoPagina2.getTotalPages());
        assertEquals(2, resultadoPagina2.getSize());
        assertEquals(1, resultadoPagina2.getPage());
    }

    @Test
    public void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Actividad actividad = new Actividad();
        Page<Actividad> actividadPage = new PageImpl<>(Collections.singletonList(actividad), pageable, 1);

        when(repoActividad.findAll((Specification<Actividad>) null, pageable)).thenReturn(actividadPage);

        PaginaRespuestaActividades<Actividad> respuesta = servicioActividad.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(actividad, respuesta.getContent().getFirst());
    }

    @Test
    void testObtenerActividadesEnTuZona() {
        // Crear datos de prueba
        Actividad actividad1 = new Actividad();
        actividad1.setId(1);
        actividad1.setNombre("actividad1");

        Actividad actividad2 = new Actividad();
        actividad2.setId(2);
        actividad2.setNombre("actividad2");

        Actividad actividad3 = new Actividad();
        actividad3.setId(3);
        actividad3.setNombre("actividad3");

        Actividad actividad4 = new Actividad();
        actividad4.setId(4);
        actividad4.setNombre("actividad4");

        Localizacion localizacion1 = new Localizacion();
        localizacion1.setNombre("Madrid");

        Localizacion localizacion2 = new Localizacion();
        localizacion2.setNombre("Barcelona");

        Localizacion localizacion3 = new Localizacion();
        localizacion3.setNombre("Sevilla");

        actividad1.setLocalizacion(localizacion1);
        actividad2.setLocalizacion(localizacion1);
        actividad3.setLocalizacion(localizacion1);
        actividad4.setLocalizacion(localizacion3);

        List<Actividad> todasLasActividades = Arrays.asList(actividad1, actividad2, actividad3, actividad4);

        // Configurar el comportamiento del mock
        when(servicioActividad.buscarEntidades()).thenReturn(todasLasActividades);

        // Ejecutar el método a probar
        List<Actividad> resultado = servicioActividad.obtenerActividadesEnTuZona(actividad1);

        // Verificar los resultados
        assertEquals(2, resultado.size());
        assertEquals(actividad2, resultado.get(0));
        assertEquals(actividad3, resultado.get(1));
    }

    @Test
    void testGetPrecioActual() {

        // Obtiene la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Crea un conjunto de precios para la actividad
        Set<Precio> preciosActividad = new HashSet<>();

        // Crea un precio válido para la fecha actual
        Precio precio = new Precio();
        precio.setFechaInicio(fechaActual.minusDays(1));
        precio.setFechaFin(fechaActual.plusDays(1));
        precio.setValor(100.0);

        // Crea un precio que no es válido para la fecha actual
        Precio precio2 = new Precio();
        precio2.setFechaInicio(fechaActual.plusMonths(2));
        precio2.setFechaFin(fechaActual.plusMonths(3));
        precio2.setValor(200.0);

        // Agrega los precios al conjunto de precios de la actividad
        preciosActividad.add(precio);
        preciosActividad.add(precio2);

        // Crea una instancia de Actividad y le asigna los precios
        Actividad actividad = new Actividad();
        actividad.setPrecio(preciosActividad);

        // Llama al método que se está probando
        Precio respuesta = servicioActividad.getPrecioActual(actividad, fechaActual);

        // Verifica que la respuesta no sea nula y que el valor del precio sea el esperado
        assertNotNull(respuesta);
        assertEquals(100.0, respuesta.getValor());
    }

    @Test
    void testObtenerPreciosActualesActividades() {
        LocalDateTime now = LocalDateTime.now();

        Actividad actividad1 = new Actividad();
        actividad1.setId(1);
        Actividad actividad2 = new Actividad();
        actividad2.setId(2);

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

        actividad1.getPrecio().add(precio1);
        actividad1.getPrecio().add(precio2);
        actividad2.getPrecio().add(precio3);
        actividad2.getPrecio().add(precio4);


        Map<Integer, Double> preciosActuales = servicioActividad.obtenerPreciosActualesActividades(Arrays.asList(actividad1, actividad2));

        assertNotNull(preciosActuales);
        assertEquals(precio1.getValor(), preciosActuales.get(actividad1.getId()));
        assertEquals(precio3.getValor(), preciosActuales.get(actividad2.getId()));
    }
}