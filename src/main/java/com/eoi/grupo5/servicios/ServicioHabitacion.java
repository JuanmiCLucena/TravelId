package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import com.eoi.grupo5.repos.RepoHabitacion;
import com.eoi.grupo5.repos.RepoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio para gestionar las operaciones relacionadas con las habitaciones.
 */
@Service
public class ServicioHabitacion extends AbstractBusinessServiceSoloEnt<Habitacion, Integer, RepoHabitacion> {

    private final RepoReserva repoReserva;

    /**
     * Constructor del servicio de habitaciones.
     *
     * @param repoHabitacion el repositorio de habitaciones.
     * @param repoReserva el repositorio de reservas.
     */
    protected ServicioHabitacion(RepoHabitacion repoHabitacion, RepoReserva repoReserva) {
        super(repoHabitacion);
        this.repoReserva = repoReserva;
    }

    /**
     * Busca las entidades de habitaciones paginadas.
     *
     * @param page el número de página.
     * @param size el tamaño de la página.
     * @return la respuesta paginada de habitaciones.
     */
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

    /**
     * Obtiene el precio actual de una habitación para una fecha específica.
     *
     * @param habitacion la habitación.
     * @param fechaActual la fecha actual.
     * @return el precio vigente en la fecha actual.
     */
    public Precio getPrecioActual(Habitacion habitacion, LocalDateTime fechaActual) {
        return habitacion.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) &&
                        (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene los precios actuales de las habitaciones de un hotel.
     *
     * @param hotel el hotel.
     * @return un mapa con los ID de las habitaciones y sus precios actuales.
     */
    public Map<Integer, Double> obtenerPreciosActualesHabitacionesHotel(Hotel hotel) {
        LocalDateTime fechaActual = LocalDateTime.now();
        Map<Integer, Double> preciosActuales = new HashMap<>();

        hotel.getHabitaciones().forEach(habitacion -> {
            Precio precioActual = getPrecioActual(habitacion, fechaActual);
            preciosActuales.put(habitacion.getId(), (precioActual != null) ? precioActual.getValor() : null);
        });

        return preciosActuales;
    }

    /**
     * Calcula el precio total de una habitación para un rango de fechas.
     *
     * @param habitacion la habitación.
     * @param fechaInicio la fecha de inicio.
     * @param fechaFin la fecha de fin.
     * @return el precio total para el rango de fechas.
     */
    public Double calcularPrecioTotal(Habitacion habitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        double precioTotal = 0.0;
        LocalDateTime fechaActual = fechaInicio;

        while (!fechaActual.isAfter(fechaFin)) {
            Precio precioVigente = getPrecioActual(habitacion, fechaActual);
            if (precioVigente != null) {
                precioTotal += precioVigente.getValor();
            }
            fechaActual = fechaActual.plusDays(1);
        }

        return precioTotal;
    }

    /**
     * Obtiene los rangos de fechas disponibles para una habitación en un rango de fechas específico.
     *
     * @param idHabitacion el ID de la habitación.
     * @param fechaInicio la fecha de inicio del rango a verificar.
     * @param fechaFin la fecha de fin del rango a verificar.
     * @return una lista de intervalos disponibles.
     */
    public List<Interval> obtenerRangosDisponibles(Integer idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Obtenemos las reservas de la habitación en concreto
        List<Reserva> reservas = repoReserva.findByHabitacionesReservadasId(idHabitacion);

        // Ordenar reservas por fecha de inicio
        reservas.sort(Comparator.comparing(Reserva::getFechaInicio));

        List<Interval> rangosDisponibles = new ArrayList<>();
        LocalDateTime currentStart = fechaInicio;

        // Verificar que la habitación existe
        Optional<Habitacion> habitacionOpt = getRepo().findById(idHabitacion);
        if (!habitacionOpt.isPresent()) {
            return rangosDisponibles; // Retornar lista vacía si la habitación no existe
        }
        Habitacion habitacion = habitacionOpt.get();

        // Iterar sobre las reservas de la habitación
        for (Reserva reserva : reservas) {
            // Si hay un intervalo disponible entre la fecha inicio y el inicio de la reserva
            if (reserva.getFechaInicio().isAfter(currentStart)) {
                // Calcular el precio total para este intervalo disponible
                Double precioTotal = calcularPrecioTotal(habitacion, currentStart, reserva.getFechaInicio());
                // Añadir el intervalo disponible a la lista de intervalos disponibles
                rangosDisponibles.add(new Interval(currentStart, reserva.getFechaInicio(), precioTotal));
            }
            // Fecha inicio será la fecha del fin de la reserva,
            // si el fin es posterior a la fecha inicio
            if (reserva.getFechaFin().isAfter(currentStart)) {
                currentStart = reserva.getFechaFin();
            }
        }

        // Si queda un intervalo disponible después de la última reserva hasta la fecha de fin
        if (currentStart.isBefore(fechaFin)) {
            Double precioTotal = calcularPrecioTotal(habitacion, currentStart, fechaFin);
            rangosDisponibles.add(new Interval(currentStart, fechaFin, precioTotal));
        }

        return rangosDisponibles;
    }

    /**
     * Clase estática para representar un intervalo de tiempo con su precio total.
     */
    public static class Interval {
        private LocalDateTime start;
        private LocalDateTime end;
        private Double intervalPrice;

        /**
         * Constructor del intervalo.
         *
         * @param start el inicio del intervalo.
         * @param end el fin del intervalo.
         * @param intervalPrice el precio total del intervalo.
         */
        public Interval(LocalDateTime start, LocalDateTime end, Double intervalPrice) {
            this.start = start;
            this.end = end;
            this.intervalPrice = intervalPrice;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public Double getIntervalPrice() {
            return intervalPrice;
        }
    }
}
