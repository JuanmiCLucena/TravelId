package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import com.eoi.grupo5.repos.RepoHabitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServicioHabitacion extends AbstractBusinessServiceSoloEnt<Habitacion, Integer, RepoHabitacion>{


    protected ServicioHabitacion(RepoHabitacion repoHabitacion) {
        super(repoHabitacion);
    }

    public PaginaRespuestaHabitaciones<Habitacion> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Habitacion> habitacionPage = getRepo().findAll(pageable);

        PaginaRespuestaHabitaciones<Habitacion> respuesta = new PaginaRespuestaHabitaciones<>();
        respuesta.setContent(habitacionPage.getContent());
        respuesta.setSize(habitacionPage.getSize());
        respuesta.setTotalSize(habitacionPage.getTotalElements());
        respuesta.setPage(habitacionPage.getNumber());
        respuesta.setTotalPages(habitacionPage.getTotalPages());

        return respuesta;
    }

    public Precio getPrecioActual(Habitacion habitacion, LocalDateTime fechaActual) {
        return habitacion.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) && (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

    public Map<Integer, Double> obtenerPreciosActualesHabitacionesHotel(Hotel hotel) {
        // Obtener los precios actuales de las habitaciones del hotel
        LocalDateTime fechaActual = LocalDateTime.now();
        Map<Integer, Double> preciosActuales = new HashMap<>();

        hotel.getHabitaciones().forEach(habitacion -> {
            Precio precioActual = getPrecioActual(habitacion, fechaActual);
            if (precioActual != null) {
                preciosActuales.put(habitacion.getId(), precioActual.getValor());
            } else {
                preciosActuales.put(habitacion.getId(), null);
            }
        });

        return preciosActuales;
    }

    public Double calcularPrecioTotal(Habitacion habitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        double precioTotal = 0.0;
        LocalDateTime fechaActual = fechaInicio;

        while (!fechaActual.isAfter(fechaFin)) {
            Precio precioVigente = getPrecioActual(habitacion, fechaActual);
            if (precioVigente != null) {
                precioTotal += precioVigente.getValor();
            }
            // Avanzar al siguiente día
            fechaActual = fechaActual.plusDays(1);
        }

        return precioTotal;
    }
}
