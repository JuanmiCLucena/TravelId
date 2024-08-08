package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.paginacion.PaginaRespuestaLocalizaciones;
import com.eoi.grupo5.paginacion.PaginaRespuestaPrecios;
import com.eoi.grupo5.repos.RepoPrecio;
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

public class ServicioPrecioTest {

    @Mock
    private RepoPrecio repoPrecio;

    private ServicioPrecio servicioPrecio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioPrecio = new ServicioPrecio(repoPrecio);
    }

    @Test
    void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Precio precio = new Precio();
        Page<Precio> precioPage = new PageImpl<>(Collections.singletonList(precio), pageable, 1);

        when(repoPrecio.findAll(pageable)).thenReturn(precioPage);

        PaginaRespuestaPrecios<Precio> respuesta = servicioPrecio.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(precio, respuesta.getContent().getFirst());
    }
}