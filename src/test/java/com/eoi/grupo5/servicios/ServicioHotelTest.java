package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
import com.eoi.grupo5.repos.RepoActividad;
import com.eoi.grupo5.repos.RepoHotel;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ServicioHotelTest {

    @Mock
    private RepoHotel repoHotel;

    private ServicioHotel servicioHotel;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioHotel = new ServicioHotel(repoHotel);
    }

    @Test
    void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Hotel hotel = new Hotel(); // Construct activity object here
        Page<Hotel> hotelPage = new PageImpl<>(Collections.singletonList(hotel), pageable, 1);

        when(repoHotel.findAll((Specification<Hotel>) null, pageable)).thenReturn(hotelPage);

        PaginaRespuestaHoteles<Hotel> respuesta = servicioHotel.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(hotel, respuesta.getContent().getFirst());
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

        Hotel hotel4 = new Hotel();
        hotel4.setId(4);
        hotel4.setNombre("hotel4");

        Localizacion localizacion1 = new Localizacion();
        localizacion1.setNombre("Madrid");

        Localizacion localizacion2 = new Localizacion();
        localizacion2.setNombre("Barcelona");

        Localizacion localizacion3 = new Localizacion();
        localizacion3.setNombre("Sevilla");

        hotel1.setLocalizacion(localizacion1);
        hotel2.setLocalizacion(localizacion1);
        hotel3.setLocalizacion(localizacion1);
        hotel4.setLocalizacion(localizacion3);

        List<Hotel> todosLosHoteles = Arrays.asList(hotel1, hotel2, hotel3, hotel4);

        // Configurar el comportamiento del mock
        when(servicioHotel.buscarEntidades()).thenReturn(todosLosHoteles);

        // Ejecutar el método a probar
        List<Hotel> resultado = servicioHotel.obtenerHotelesEnTuZona(hotel1);

        // Verificar los resultados
        assertEquals(2, resultado.size());
        assertEquals(hotel2, resultado.get(0));
        assertEquals(hotel3, resultado.get(1));
    }
}