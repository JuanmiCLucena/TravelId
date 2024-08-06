package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.repos.RepoHabitacion;
import com.eoi.grupo5.repos.RepoReserva;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServicioHabitacionTest {

    @Mock
    private RepoHabitacion repoHabitacion;
    
    @Mock
    private RepoReserva repoReserva;
    
    @Mock
    private ServicioHotel servicioHotel;

    private ServicioHabitacion servicioHabitacion;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioHabitacion = new ServicioHabitacion(repoHabitacion, repoReserva, servicioHotel);
    }

    @Test
    public void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Habitacion habitacion = new Habitacion();
        Page<Habitacion> habitacionPage = new PageImpl<>(Collections.singletonList(habitacion), pageable, 1);

        when(repoHabitacion.findAll(pageable)).thenReturn(habitacionPage);

        PaginaRespuestaHabitaciones<Habitacion> respuesta = servicioHabitacion.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(habitacion, respuesta.getContent().getFirst());
    }

    @Test
    void testGetPrecioActual() {
        // Obtiene la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Crea un conjunto de precios para la actividad
        Set<Precio> preciosHabitacion = new HashSet<>();

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
        preciosHabitacion.add(precio);
        preciosHabitacion.add(precio2);

        // Crea una instancia de Actividad y le asigna los precios
        Habitacion habitacion = new Habitacion();
        habitacion.setPrecio(preciosHabitacion);

        // Llama al método que se está probando
        Precio respuesta = servicioHabitacion.getPrecioActual(habitacion, fechaActual);

        // Verifica que la respuesta no sea nula y que el valor del precio sea el esperado
        assertNotNull(respuesta);
        assertEquals(100.0, respuesta.getValor());
    }

    @Test
    void testObtenerPreciosActualesHabitacionesHotel() {
        LocalDateTime now = LocalDateTime.now();

        Habitacion habitacion1 = new Habitacion();
        habitacion1.setId(1);
        Habitacion habitacion2 = new Habitacion();
        habitacion2.setId(2);

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

        habitacion1.getPrecio().add(precio1);
        habitacion1.getPrecio().add(precio2);
        habitacion2.getPrecio().add(precio3);
        habitacion2.getPrecio().add(precio4);

        Hotel hotel1 = new Hotel();

        hotel1.getHabitaciones().add(habitacion1);
        hotel1.getHabitaciones().add(habitacion2);

        Map<Integer, Double> preciosActuales = servicioHabitacion.obtenerPreciosActualesHabitacionesHotel(hotel1);

        assertNotNull(preciosActuales);
        assertEquals(precio1.getValor(), preciosActuales.get(habitacion1.getId()));
        assertEquals(precio3.getValor(), preciosActuales.get(habitacion2.getId()));
    }

    @Test
    void testCalcularPrecioTotal() {
        Habitacion habitacion1 = new Habitacion();
        LocalDateTime fechaInicio = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime fechaFin = fechaInicio.plusWeeks(1);

        Precio precio1 = new Precio();
        precio1.setValor(80.0);
        precio1.setFechaInicio(fechaInicio);
        precio1.setFechaFin(fechaInicio.plusDays(1).minusNanos(1)); // Hasta el final del primer día

        Precio precio2 = new Precio();
        precio2.setValor(120.0);
        precio2.setFechaInicio(fechaInicio.plusDays(1)); // Desde el segundo día
        precio2.setFechaFin(fechaFin);

        Set<Precio> precios = new HashSet<>();
        precios.add(precio1);
        precios.add(precio2);
        habitacion1.setPrecio(precios);

        Double precioTotal = servicioHabitacion.calcularPrecioTotal(habitacion1, fechaInicio, fechaFin);

        assertEquals(920.0, precioTotal);
    }

    @Test
    void testObtenerRangosDisponibles() {

    }

    @Test
    void testObtenerHotelesEnTuZona() {

    }


}