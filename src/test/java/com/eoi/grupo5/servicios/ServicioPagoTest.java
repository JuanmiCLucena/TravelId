package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.modelos.Pago;
import com.eoi.grupo5.paginacion.PaginaRespuestaLocalizaciones;
import com.eoi.grupo5.paginacion.PaginaRespuestaPagos;
import com.eoi.grupo5.repos.RepoPago;
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

public class ServicioPagoTest {

    @Mock
    private RepoPago repoPago;

    private ServicioPago servicioPago;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        servicioPago = new ServicioPago(repoPago);
    }

    @Test
    void testBuscarEntidadesPaginadas() {
        Pageable pageable = PageRequest.of(0, 1);
        Pago pago = new Pago();
        Page<Pago> pagoPage = new PageImpl<>(Collections.singletonList(pago), pageable, 1);

        when(repoPago.findAll(pageable)).thenReturn(pagoPage);

        PaginaRespuestaPagos<Pago> respuesta = servicioPago.buscarEntidadesPaginadas(0, 1);

        assertEquals(1, respuesta.getTotalSize());
        assertEquals(pago, respuesta.getContent().getFirst());
    }
}