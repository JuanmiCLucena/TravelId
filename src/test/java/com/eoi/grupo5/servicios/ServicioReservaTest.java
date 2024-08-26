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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    @Mock
    private RepoReservaActividad repoReservaActividad;

    private ServicioReserva servicioReserva;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioReserva = new ServicioReserva(repoReserva, repoHabitacion, repoAsiento, repoActividad,repoReservaActividad, repoPago, repoMetodoPago);
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
        // Configuración de la Reserva
        Reserva reserva = new Reserva();
        reserva.setId(1);

        // Configuración de la Actividad
        Actividad actividad = new Actividad();
        actividad.setId(1);
        actividad.setMaximosAsistentes(10);
        actividad.setAsistentesConfirmados(0);
        actividad.setFechaInicio(LocalDateTime.of(2024, 8, 1, 12, 0));
        actividad.setFechaFin(LocalDateTime.of(2024, 8, 5, 12, 0));

        // Datos de la reserva de actividad
        LocalDateTime fechaInicio = LocalDateTime.of(2024, 8, 1, 14, 0);
        LocalDateTime fechaFin = LocalDateTime.of(2024, 8, 5, 11, 0);
        Integer asistentes = 5;

        // Configurar mocks
        when(repoActividad.findById(1)).thenReturn(Optional.of(actividad));
        when(repoReservaActividad.findByReservaAndActividad(reserva, actividad)).thenReturn(Optional.empty());
        when(repoReservaActividad.save(any(ReservaActividad.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repoReserva.save(any(Reserva.class))).thenReturn(reserva);

        // Ejecutar el método bajo prueba
        servicioReserva.addActividad(reserva, 1, fechaInicio, fechaFin, asistentes);

        // Verificar que se llamó a los métodos del repositorio correctamente
        verify(repoActividad, times(1)).findById(1);
        verify(repoReservaActividad, times(1)).findByReservaAndActividad(reserva, actividad);
        verify(repoReservaActividad, times(1)).save(any(ReservaActividad.class));
        verify(repoActividad, times(1)).save(actividad);
        verify(repoReserva, times(1)).save(reserva);

        // Verificar que la relación se ha establecido correctamente
        assertEquals(1, reserva.getReservaActividades().size());
        ReservaActividad reservaActividad = reserva.getReservaActividades().iterator().next();
        assertEquals(actividad, reservaActividad.getActividad());
        assertEquals(reserva, reservaActividad.getReserva());
        assertEquals(asistentes, reservaActividad.getAsistentes());
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
    public void testObtenerReservasPorUsuarioPaginadas() {
        // Configurar el usuario
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombreUsuario("Test User");

        // Crear una lista de reservas de ejemplo
        List<Reserva> reservas = Arrays.asList(new Reserva(), new Reserva());

        // Configurar el Pageable
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);

        // Crear un Page de reservas para simular la respuesta de la consulta paginada
        Page<Reserva> reservaPage = new PageImpl<>(reservas, pageable, reservas.size());

        // Configurar el comportamiento del repositorio para devolver el Page de reservas
        when(repoReserva.findByUsu(usuario, pageable)).thenReturn(reservaPage);

        // Llamar al método de servicio
        PaginaRespuestaReservas<Reserva> result = servicioReserva.obtenerReservasPorUsuarioPaginadas(usuario, page, size);

        // Verificar que se llamó al método del repositorio con los parámetros correctos
        verify(repoReserva, times(1)).findByUsu(usuario, pageable);

        // Comprobaciones del resultado
        assertNotNull(result);
        assertEquals(reservas, result.getContent());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(1, result.getTotalPages());
        assertEquals(reservas.size(), result.getTotalSize());
    }
}