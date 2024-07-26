package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaAsientos;
import com.eoi.grupo5.paginacion.PaginaRespuestaPrecios;
import com.eoi.grupo5.repos.RepoAsiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServicioAsiento extends AbstractBusinessServiceSoloEnt<Asiento, Integer, RepoAsiento>{

    protected ServicioAsiento(RepoAsiento repoAsiento) {
        super(repoAsiento);
    }

    public PaginaRespuestaAsientos<Asiento> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Asiento> asientoPage = getRepo().findAll(pageable);

        PaginaRespuestaAsientos<Asiento> respuesta = new PaginaRespuestaAsientos<>();
        respuesta.setContent(asientoPage.getContent());
        respuesta.setSize(asientoPage.getSize());
        respuesta.setTotalSize(asientoPage.getTotalElements());
        respuesta.setPage(asientoPage.getNumber());
        respuesta.setTotalPages(asientoPage.getTotalPages());

        return respuesta;
    }

    public Precio getPrecioActual(Asiento asiento, LocalDateTime fechaActual) {
        return asiento.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) && (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

    public Map<Integer, Double> obtenerPreciosActualesAsientosVuelo(Vuelo vuelo) {
        // Obtener los precios actuales de las habitaciones del hotel
        LocalDateTime fechaActual = LocalDateTime.now();
        Map<Integer, Double> preciosActuales = new HashMap<>();

        vuelo.getAsientos().forEach(asiento -> {
            Precio precioActual = getPrecioActual(asiento, fechaActual);
            if (precioActual != null) {
                preciosActuales.put(asiento.getId(), precioActual.getValor());
            } else {
                preciosActuales.put(asiento.getId(), null);
            }
        });

        return preciosActuales;
    }
}
