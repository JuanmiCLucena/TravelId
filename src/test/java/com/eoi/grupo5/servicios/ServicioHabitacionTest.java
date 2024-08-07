package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.repos.RepoHabitacion;
import com.eoi.grupo5.repos.RepoReserva;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    public void testObtenerRangosDisponibles() {
        Integer idHabitacion = 1;
        LocalDateTime fechaEntrada = LocalDateTime.of(2024, 8, 1, 14, 0);
        LocalDateTime fechaSalida = LocalDateTime.of(2024, 8, 10, 11, 0);

        Habitacion habitacion = new Habitacion();
        habitacion.setId(idHabitacion);

        List<Reserva> reservas = new ArrayList<>();
        Reserva reserva1 = new Reserva();
        reserva1.setFechaInicio(LocalDateTime.of(2024, 8, 2, 12, 0));
        reserva1.setFechaFin(LocalDateTime.of(2024, 8, 5, 10, 0));
        reservas.add(reserva1);

        Reserva reserva2 = new Reserva();
        reserva2.setFechaInicio(LocalDateTime.of(2024, 8, 6, 14, 0));
        reserva2.setFechaFin(LocalDateTime.of(2024, 8, 8, 12, 0));
        reservas.add(reserva2);


        when(repoReserva.findByHabitacionesReservadasId(idHabitacion)).thenReturn(reservas);
        when(repoHabitacion.findById(idHabitacion)).thenReturn(Optional.of(habitacion));

        List<ServicioHabitacion.Interval> rangosDisponibles = servicioHabitacion.obtenerRangosDisponibles(idHabitacion, fechaEntrada, fechaSalida);

        assertEquals(3, rangosDisponibles.size());
        assertEquals(fechaEntrada, rangosDisponibles.get(0).getStart());
        assertEquals(LocalDateTime.of(2024, 8, 2, 12, 0), rangosDisponibles.get(0).getEnd());
        assertEquals(LocalDateTime.of(2024, 8, 5, 10, 0), rangosDisponibles.get(1).getStart());
        assertEquals(LocalDateTime.of(2024, 8, 6, 14, 0), rangosDisponibles.get(1).getEnd());
    }

    @Test
    public void testObtenerRangosDisponiblesHabitacionNoExiste() {
        Integer idHabitacion = 1;
        LocalDateTime fechaEntrada = LocalDateTime.of(2024, 8, 1, 14, 0);
        LocalDateTime fechaSalida = LocalDateTime.of(2024, 8, 10, 11, 0);

        when(repoHabitacion.findById(idHabitacion)).thenReturn(Optional.empty());

        List<ServicioHabitacion.Interval> rangosDisponibles = servicioHabitacion.obtenerRangosDisponibles(idHabitacion, fechaEntrada, fechaSalida);

        assertTrue(rangosDisponibles.isEmpty());
    }

    @Test
    void testObtenerHotelesEnTuZona() {
        // Crear datos de prueba
        Hotel hotel1 = new Hotel();
        hotel1.setId(1);
        hotel1.setNombre("hotel1");

        Hotel hotel2 = new Hotel();
        hotel2.setId(2);
        hotel2.setNombre("hotel2");

        Hotel hotel3 = new Hotel();
        hotel3.setId(3);
        hotel3.setNombre("hotel3");

        Habitacion habitacion1 = new Habitacion();
        habitacion1.setId(3);
        habitacion1.setHotel(hotel1);

        Localizacion localizacion1 = new Localizacion();
        localizacion1.setNombre("Madrid");

        Localizacion localizacion2 = new Localizacion();
        localizacion2.setNombre("Barcelona");

        Localizacion localizacion3 = new Localizacion();
        localizacion3.setNombre("Sevilla");

        hotel1.setLocalizacion(localizacion1);
        hotel2.setLocalizacion(localizacion1);
        hotel3.setLocalizacion(localizacion2);

        List<Hotel> todosLosHoteles = Arrays.asList(hotel1, hotel2, hotel3);

        // Configurar el comportamiento del mock
        when(servicioHotel.buscarEntidades()).thenReturn(todosLosHoteles);

        // Ejecutar el método a probar
        List<Hotel> resultado = servicioHabitacion.obtenerHotelesEnTuZona(habitacion1);

        // Verificar los resultados
        assertEquals(1, resultado.size());
        assertEquals(hotel2, resultado.getFirst());
        assertNotEquals(habitacion1.getHotel().getLocalizacion().getNombre(), hotel3.getLocalizacion().getNombre());
    }


}