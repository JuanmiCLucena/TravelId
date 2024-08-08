package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaReservas;
import com.eoi.grupo5.repos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioReservaTest {

    @Mock
    private RepoReserva repoReserva;

    @Mock
    private RepoHabitacion repoHabitacion;

    @Mock
    private RepoAsiento repoAsiento;

    @Mock
    private RepoActividad repoActividad;

    @Mock
    private RepoPago repoPago;

    @Mock
    private RepoMetodoPago repoMetodoPago;

    private ServicioReserva servicioReserva;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioReserva = new ServicioReserva(repoReserva, repoHabitacion, repoAsiento, repoActividad, repoPago, repoMetodoPago);
    }

    @Test
    void testCrearReserva() {

        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombreUsuario("Test User");

        LocalDateTime fechaInicio = LocalDateTime.of(2024, 8, 1, 14, 0);
        LocalDateTime fechaFin = LocalDateTime.of(2024, 8, 5, 11, 0);

        Reserva reserva = new Reserva();
        reserva.setUsu(usuario);
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);

        // Configuramos el mock para que devuelva la reserva cuando se guarda
        when(repoReserva.save(any(Reserva.class))).thenReturn(reserva);

        Reserva result = servicioReserva.crearReserva(usuario, fechaInicio, fechaFin);

        // Verificamos que el método save del repoReserva fue llamado una vez
        verify(repoReserva, times(1)).save(any(Reserva.class));

        // Verificamos que los valores de la reserva devuelta sean correctos
        assertNotNull(result);
        assertEquals(usuario, result.getUsu());
        assertEquals(fechaInicio, result.getFechaInicio());
        assertEquals(fechaFin, result.getFechaFin());
    }

    @Test
    public void testAddHabitacion() {
        Reserva reserva = new Reserva();
        reserva.setId(1);

        Habitacion habitacion = new Habitacion();
        habitacion.setId(1);

        LocalDateTime fechaInicio = LocalDateTime.of(2024, 8, 1, 14, 0);
        LocalDateTime fechaFin = LocalDateTime.of(2024, 8, 5, 11, 0);

        when(repoHabitacion.findById(1)).thenReturn(Optional.of(habitacion));
        when(repoReserva.save(any(Reserva.class))).thenReturn(reserva);

        servicioReserva.addHabitacion(reserva, 1, fechaInicio, fechaFin);

        verify(repoHabitacion, times(1)).findById(1);
        verify(repoReserva, times(1)).save(reserva);

        assertTrue(reserva.getHabitacionesReservadas().contains(habitacion));
        assertEquals(fechaInicio, reserva.getFechaInicio());
        assertEquals(fechaFin, reserva.getFechaFin());
    }

    @Test
    public void testAddActividad() {
        Reserva reserva = new Reserva();
        reserva.setId(1);

        Actividad actividad = new Actividad();
        actividad.setId(1);
        actividad.setFechaInicio(LocalDateTime.of(2024, 8, 1, 12, 0));
        actividad.setFechaFin(LocalDateTime.of(2024, 8, 5, 12, 0));
        actividad.setAsistentesConfirmados(0);
        actividad.setMaximosAsistentes(10);

        LocalDateTime fechaInicio = LocalDateTime.of(2024, 8, 1, 14, 0);
        LocalDateTime fechaFin = LocalDateTime.of(2024, 8, 5, 11, 0);
        Integer asistentes = 5;

        when(repoActividad.findById(1)).thenReturn(Optional.of(actividad));
        when(repoActividad.save(any(Actividad.class))).thenReturn(actividad);
        when(repoReserva.save(any(Reserva.class))).thenReturn(reserva);

        servicioReserva.addActividad(reserva, 1, fechaInicio, fechaFin, asistentes);

        verify(repoActividad, times(1)).findById(1);
        verify(repoActividad, times(1)).save(actividad);
        verify(repoReserva, times(1)).save(reserva);

        assertTrue(reserva.getActividadesReservadas().contains(actividad));
        assertEquals(asistentes, actividad.getAsistentesConfirmados());
    }

    @Test
    public void testAddAsiento() {
        Reserva reserva = new Reserva();
        reserva.setId(1);

        Asiento asiento = new Asiento();
        asiento.setId(1);

        when(repoReserva.findById(1)).thenReturn(Optional.of(reserva));
        when(repoAsiento.findById(1)).thenReturn(Optional.of(asiento));
        when(repoReserva.save(any(Reserva.class))).thenReturn(reserva);

        servicioReserva.addAsiento(1, 1);

        verify(repoReserva, times(1)).findById(1);
        verify(repoAsiento, times(1)).findById(1);
        verify(repoReserva, times(1)).save(reserva);

        assertTrue(reserva.getAsientosReservados().contains(asiento));
    }

//    @Test
//    void testConfirmarReserva() {
//    }

    @Test
    public void testCancelarReserva() {
        Reserva reserva = new Reserva();
        reserva.setId(1);
        reserva.setCancelado(false);

        when(repoReserva.findById(1)).thenReturn(Optional.of(reserva));
        when(repoReserva.save(any(Reserva.class))).thenReturn(reserva);

        servicioReserva.cancelarReserva(1);

        verify(repoReserva, times(1)).findById(1);
        verify(repoReserva, times(1)).save(reserva);

        assertTrue(reserva.isCancelado());
    }

    @Test
    public void testGenerarPago() {
        Reserva reserva = new Reserva();
        reserva.setId(1);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(1);

        Double precioTotal = 100.0;
        Integer metodoPagoId = 1;

        when(repoMetodoPago.findById(metodoPagoId)).thenReturn(Optional.of(metodoPago));
        when(repoPago.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repoReserva.save(any(Reserva.class))).thenReturn(reserva);

        servicioReserva.generarPago(reserva, precioTotal, metodoPagoId);

        verify(repoMetodoPago, times(1)).findById(metodoPagoId);
        verify(repoPago, times(1)).save(any(Pago.class));
        verify(repoReserva, times(1)).save(reserva);

        assertNotNull(reserva.getPago());
        assertEquals(precioTotal, reserva.getPago().getImporte());
        assertEquals(metodoPago, reserva.getPago().getMetodoPago());
    }

    @Test
    public void testBuscarEntidadesPaginadas() {
        int page = 0;
        int size = 10;

        Page<Reserva> reservaPage = mock(Page.class);
        List<Reserva> reservaList = Arrays.asList(new Reserva(), new Reserva());

        when(reservaPage.getContent()).thenReturn(reservaList);
        when(reservaPage.getSize()).thenReturn(size);
        when(reservaPage.getTotalElements()).thenReturn(20L);
        when(reservaPage.getNumber()).thenReturn(page);
        when(reservaPage.getTotalPages()).thenReturn(2);
        when(repoReserva.findAll(any(Pageable.class))).thenReturn(reservaPage);

        PaginaRespuestaReservas<Reserva> respuesta = servicioReserva.buscarEntidadesPaginadas(page, size);

        verify(repoReserva, times(1)).findAll(any(Pageable.class));

        assertNotNull(respuesta);
        assertEquals(reservaList, respuesta.getContent());
        assertEquals(size, respuesta.getSize());
        assertEquals(20L, respuesta.getTotalSize());
        assertEquals(page, respuesta.getPage());
        assertEquals(2, respuesta.getTotalPages());
    }

    @Test
    public void testObtenerReservasPorUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombreUsuario("Test User");

        List<Reserva> reservas = Arrays.asList(new Reserva(), new Reserva());

        when(repoReserva.findByUsu(usuario)).thenReturn(reservas);

        List<Reserva> result = servicioReserva.obtenerReservasPorUsuario(usuario);

        verify(repoReserva, times(1)).findByUsu(usuario);

        assertNotNull(result);
        assertEquals(reservas, result);
    }
}