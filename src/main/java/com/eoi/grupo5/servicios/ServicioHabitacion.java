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
 * Proporciona métodos para manejar el CRUD, obtener precios y rangos de disponibilidad,
 * y realizar otras operaciones específicas de la entidad {@link Habitacion}.
 */
@Service
public class ServicioHabitacion extends AbstractBusinessServiceSoloEnt<Habitacion, Integer, RepoHabitacion> {

    private final RepoReserva repoReserva;
    private final ServicioHotel servicioHotel;

    /**
     * Constructor del servicio de habitaciones.
     *
     * @param repoHabitacion el repositorio de habitaciones.
     * @param repoReserva el repositorio de reservas.
     * @param servicioHotel el servicio para gestionar hoteles.
     */
    protected ServicioHabitacion(RepoHabitacion repoHabitacion, RepoReserva repoReserva, ServicioHotel servicioHotel) {
        super(repoHabitacion);
        this.repoReserva = repoReserva;
        this.servicioHotel = servicioHotel;
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
     * @param habitacion la habitación a evaluar.
     * @param fechaActual la fecha actual.
     * @return el precio vigente en la fecha actual, o {@code null} si no hay un precio válido.
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
     * @param hotel el hotel para el cual se desean obtener los precios de las habitaciones.
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
     * @param habitacion la habitación a evaluar.
     * @param fechaInicio la fecha de inicio del rango.
     * @param fechaFin la fecha de fin del rango.
     * @return el precio total para el rango de fechas especificado.
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
     * @param fechaEntrada la fecha de inicio del rango a verificar.
     * @param fechaSalida la fecha de fin del rango a verificar.
     * @return una lista de intervalos disponibles, cada uno con su precio total.
     */
    public List<Interval> obtenerRangosDisponibles(Integer idHabitacion, LocalDateTime fechaEntrada, LocalDateTime fechaSalida) {
        // Obtenemos las reservas de la habitación en concreto
        List<Reserva> reservas = repoReserva.findByHabitacionesReservadasId(idHabitacion);

        // Filtramos excluyendo las reservas que hayan sido canceladas
        List<Reserva> reservasValidas = new ArrayList<>(reservas.stream().filter(reserva -> !reserva.isCancelado()).toList());

        // Ordenar reservas por fecha de inicio
        reservasValidas.sort(Comparator.comparing(Reserva::getFechaInicio));

        List<Interval> rangosDisponibles = new ArrayList<>();
        LocalDateTime fechaInicio = fechaEntrada;

        // Verificar que la habitación existe
        Optional<Habitacion> habitacionOpt = getRepo().findById(idHabitacion);
        if (!habitacionOpt.isPresent()) {
            return rangosDisponibles; // Retornar lista vacía si la habitación no existe
        }
        Habitacion habitacion = habitacionOpt.get();

        // Iterar sobre las reservas de la habitación
        for (Reserva reserva : reservasValidas) {
            // Si hay un intervalo disponible entre la fecha inicio y el inicio de la reserva
            if (reserva.getFechaInicio().isAfter(fechaInicio)) {
                // Calcular el precio total para este intervalo disponible
                Double precioTotal = calcularPrecioTotal(habitacion, fechaInicio, reserva.getFechaInicio());
                // Añadir el intervalo disponible a la lista de intervalos disponibles
                rangosDisponibles.add(new Interval(fechaInicio, reserva.getFechaInicio(), precioTotal));
            }
            // Fecha inicio será la fecha del fin de la reserva,
            // si el fin es posterior a la fecha inicio
            if (reserva.getFechaFin().isAfter(fechaInicio)) {
                fechaInicio = reserva.getFechaFin();
            }
        }

        // Si queda un intervalo disponible después de la última reserva hasta la fecha de fin
        if (fechaInicio.isBefore(fechaSalida)) {
            Double precioTotal = calcularPrecioTotal(habitacion, fechaInicio, fechaSalida);
            rangosDisponibles.add(new Interval(fechaInicio, fechaSalida, precioTotal));
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

        /**
         * Obtiene la fecha de inicio del intervalo.
         *
         * @return la fecha de inicio.
         */
        public LocalDateTime getStart() {
            return start;
        }

        /**
         * Obtiene la fecha de fin del intervalo.
         *
         * @return la fecha de fin.
         */
        public LocalDateTime getEnd() {
            return end;
        }

        /**
         * Obtiene el precio total del intervalo.
         *
         * @return el precio total.
         */
        public Double getIntervalPrice() {
            return intervalPrice;
        }
    }

    /**
     * Obtiene una lista de hoteles en la misma zona que la habitación especificada.
     * Limita el resultado a 2 hoteles distintos del hotel de la habitación.
     *
     * @param habitacion la habitación cuya zona se quiere consultar.
     * @return una lista de hoteles en la misma zona, excluyendo el hotel actual.
     */
    public List<Hotel> obtenerHotelesEnTuZona(Habitacion habitacion) {
        Hotel hotel = habitacion.getHotel();
        return servicioHotel.buscarEntidades()
                .stream()
                .filter(h -> h.getLocalizacion().getNombre().equals(hotel.getLocalizacion().getNombre()) && !Objects.equals(h.getId(), hotel.getId()))
                .limit(2)
                .toList();
    }
}
