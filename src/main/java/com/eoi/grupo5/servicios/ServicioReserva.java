package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaReservas;
import com.eoi.grupo5.repos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Servicio para gestionar las operaciones relacionadas con las reservas.
 * Este servicio extiende de {@link AbstractBusinessServiceSoloEnt} para proporcionar
 * funcionalidades CRUD específicas para la entidad {@link Reserva}.
 */
@Service
public class ServicioReserva extends AbstractBusinessServiceSoloEnt<Reserva, Integer, RepoReserva> {

    private final RepoReserva repoReserva;
    private final RepoHabitacion repoHabitacion;
    private final RepoAsiento repoAsiento;
    private final RepoActividad repoActividad;
    private final RepoReservaActividad repoReservaActividad;
    private final RepoPago repoPago;
    private final RepoMetodoPago repoMetodoPago;

    /**
     * Constructor del servicio de reservas.
     *
     * @param repoReserva el repositorio de reservas.
     * @param repoHabitacion el repositorio de habitaciones.
     * @param repoAsiento el repositorio de asientos.
     * @param repoActividad el repositorio de actividades.
     * @param repoReservaActividad el repositorio de reservas de actividades.
     * @param repoPago el repositorio de pagos.
     * @param repoMetodoPago el repositorio de métodos de pago.
     */
    protected ServicioReserva(RepoReserva repoReserva, RepoHabitacion repoHabitacion, RepoAsiento repoAsiento,
                              RepoActividad repoActividad, RepoReservaActividad repoReservaActividad,
                              RepoPago repoPago, RepoMetodoPago repoMetodoPago) {
        super(repoReserva);
        this.repoReserva = repoReserva;
        this.repoHabitacion = repoHabitacion;
        this.repoAsiento = repoAsiento;
        this.repoActividad = repoActividad;
        this.repoReservaActividad = repoReservaActividad;
        this.repoPago = repoPago;
        this.repoMetodoPago = repoMetodoPago;
    }

    /**
     * Crea una nueva reserva para un usuario con las fechas especificadas.
     *
     * @param usuario el usuario que realiza la reserva.
     * @param fechaInicio la fecha de inicio de la reserva.
     * @param fechaFin la fecha de fin de la reserva.
     * @return la reserva creada.
     */
    public Reserva crearReserva(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Reserva reserva = new Reserva();
        reserva.setUsu(usuario);
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        return repoReserva.save(reserva);
    }

    /**
     * Añade una habitación a una reserva existente.
     *
     * @param reserva la reserva a la que se añadirá la habitación.
     * @param idHabitacion el ID de la habitación a añadir.
     * @param fechaInicio la fecha de inicio para la habitación en la reserva.
     * @param fechaFin la fecha de fin para la habitación en la reserva.
     * @throws RuntimeException si la habitación no se encuentra.
     */
    public void addHabitacion(Reserva reserva, Integer idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Optional<Habitacion> optionalHabitacion = repoHabitacion.findById(idHabitacion);
        if (optionalHabitacion.isPresent()) {
            Habitacion habitacion = optionalHabitacion.get();
            reserva.getHabitacionesReservadas().add(habitacion);
            reserva.setFechaInicio(fechaInicio);
            reserva.setFechaFin(fechaFin);
            repoReserva.save(reserva);
        } else {
            throw new RuntimeException("No se encontró la habitación.");
        }
    }

    /**
     * Añade una actividad a una reserva existente.
     *
     * @param reserva la reserva a la que se añadirá la actividad.
     * @param idActividad el ID de la actividad a añadir.
     * @param fechaInicio la fecha de inicio para la actividad en la reserva.
     * @param fechaFin la fecha de fin para la actividad en la reserva.
     * @param asistentes el número de asistentes a la actividad.
     * @throws RuntimeException si la actividad no se encuentra, si las fechas no se superponen,
     *         o si la actividad ha alcanzado el máximo de asistentes.
     */
    public void addActividad(Reserva reserva, Integer idActividad, LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer asistentes) {
        Optional<Actividad> optionalActividad = repoActividad.findById(idActividad);
        if (optionalActividad.isPresent()) {
            Actividad actividad = optionalActividad.get();
            if (actividad.getFechaInicio().isBefore(fechaFin) && actividad.getFechaFin().isAfter(fechaInicio)) {
                if ((actividad.getAsistentesConfirmados() + asistentes) <= actividad.getMaximosAsistentes()) {
                    Optional<ReservaActividad> optionalReservaActividad = repoReservaActividad.findByReservaAndActividad(reserva, actividad);
                    ReservaActividad reservaActividad;

                    if (optionalReservaActividad.isPresent()) {
                        reservaActividad = optionalReservaActividad.get();
                        reservaActividad.setAsistentes(reservaActividad.getAsistentes() + asistentes);
                    } else {
                        reservaActividad = new ReservaActividad();
                        reservaActividad.setReserva(reserva);
                        reservaActividad.setActividad(actividad);
                        reservaActividad.setAsistentes(asistentes);
                        reserva.getReservaActividades().add(reservaActividad);
                    }

                    repoReservaActividad.save(reservaActividad);
                    actividad.setAsistentesConfirmados(actividad.getAsistentesConfirmados() + asistentes);
                    repoActividad.save(actividad);
                    repoReserva.save(reserva);
                } else {
                    throw new RuntimeException("La actividad ha alcanzado el máximo de asistentes.");
                }
            } else {
                throw new RuntimeException("Las fechas de la actividad no se superponen con las de la reserva.");
            }
        } else {
            throw new RuntimeException("No se encontró la actividad.");
        }
    }

    /**
     * Añade un asiento a una reserva existente.
     *
     * @param reservaId el ID de la reserva a la que se añadirá el asiento.
     * @param idAsiento el ID del asiento a añadir.
     * @throws RuntimeException si la reserva o el asiento no se encuentran.
     */
    public void addAsiento(Integer reservaId, Integer idAsiento) {
        Optional<Reserva> optionalReserva = repoReserva.findById(reservaId);
        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();
            Optional<Asiento> optionalAsiento = repoAsiento.findById(idAsiento);
            if (optionalAsiento.isPresent()) {
                Asiento asiento = optionalAsiento.get();
                reserva.getAsientosReservados().add(asiento);
                repoReserva.save(reserva);
            } else {
                throw new RuntimeException("No se encontró el asiento.");
            }
        } else {
            throw new RuntimeException("No se encontró la reserva.");
        }
    }

    /**
     * Cancela una reserva existente.
     *
     * @param reservaId el ID de la reserva a cancelar.
     * @throws RuntimeException si la reserva no se encuentra.
     */
    public void cancelarReserva(Integer reservaId) {
        Optional<Reserva> optionalReserva = repoReserva.findById(reservaId);
        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();
            reserva.setCancelado(true);
            repoReserva.save(reserva);
        } else {
            throw new RuntimeException("No se encontró la reserva.");
        }
    }

    /**
     * Genera un pago para una reserva.
     *
     * @param reserva la reserva para la cual se generará el pago.
     * @param precioTotal el importe total a pagar.
     * @param metodoPagoId el ID del método de pago.
     * @throws RuntimeException si el método de pago no se encuentra.
     */
    public void generarPago(Reserva reserva, Double precioTotal, Integer metodoPagoId) {
        Optional<MetodoPago> optionalMetodoPago = repoMetodoPago.findById(metodoPagoId);
        if (optionalMetodoPago.isPresent()) {
            MetodoPago metodoPago = optionalMetodoPago.get();
            Pago pago = new Pago();
            pago.setImporte(precioTotal);
            pago.setFechaPago(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            pago.setMetodoPago(metodoPago);
            pago.setReserva(reserva);

            reserva.setPago(pago);

            repoPago.save(pago);
            repoReserva.save(reserva);
        } else {
            throw new RuntimeException("No se encontró el método de pago.");
        }
    }

    /**
     * Busca las reservas paginadas.
     *
     * @param page el número de página a recuperar.
     * @param size el tamaño de la página a recuperar.
     * @return una instancia de {@link PaginaRespuestaReservas} que contiene la página solicitada de reservas.
     */
    public PaginaRespuestaReservas<Reserva> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reserva> reservaPage = getRepo().findAll(pageable);

        PaginaRespuestaReservas<Reserva> respuesta = new PaginaRespuestaReservas<>();
        respuesta.setContent(reservaPage.getContent());
        respuesta.setSize(reservaPage.getSize());
        respuesta.setTotalSize(reservaPage.getTotalElements());
        respuesta.setPage(reservaPage.getNumber());
        respuesta.setTotalPages(reservaPage.getTotalPages());

        return respuesta;
    }

    /**
     * Obtiene las reservas de un usuario paginadas.
     *
     * @param usuario el usuario cuyas reservas se van a obtener.
     * @param page el número de página a recuperar.
     * @param size el tamaño de la página a recuperar.
     * @return una instancia de {@link PaginaRespuestaReservas} que contiene las reservas del usuario en la página solicitada.
     */
    public PaginaRespuestaReservas<Reserva> obtenerReservasPorUsuarioPaginadas(Usuario usuario, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reserva> reservaPage = repoReserva.findByUsu(usuario, pageable);

        PaginaRespuestaReservas<Reserva> respuesta = new PaginaRespuestaReservas<>();
        respuesta.setContent(reservaPage.getContent());
        respuesta.setSize(reservaPage.getSize());
        respuesta.setTotalSize(reservaPage.getTotalElements());
        respuesta.setPage(reservaPage.getNumber());
        respuesta.setTotalPages(reservaPage.getTotalPages());

        return respuesta;
    }

    /**
     * Obtiene los detalles de una reserva en formato de texto.
     *
     * @param reserva la reserva para la cual se obtendrán los detalles.
     * @return una cadena de texto que contiene los detalles de la reserva.
     */
    public String obtenerDetallesReserva(Reserva reserva) {
        StringBuilder detalles = new StringBuilder();

        // Detalles de habitaciones reservadas
        if (reserva.getHabitacionesReservadas() != null && !reserva.getHabitacionesReservadas().isEmpty()) {
            detalles.append("Habitaciones Reservadas:\n");
            for (Habitacion habitacion : reserva.getHabitacionesReservadas()) {
                detalles.append("  - Habitación: ").append(habitacion.getNumero()).append("\n");
            }
        }

        // Detalles de actividades reservadas
        if (reserva.getReservaActividades() != null && !reserva.getReservaActividades().isEmpty()) {
            detalles.append("Actividades Reservadas:\n");
            for (ReservaActividad reservaActividad : reserva.getReservaActividades()) {
                Actividad actividad = reservaActividad.getActividad();
                detalles.append("  - Actividad: ").append(actividad.getNombre()).append("\n");
                detalles.append("    Plazas Reservadas: ").append(reservaActividad.getAsistentes()).append("\n");
            }
        }

        // Detalles de asientos de vuelo reservados
        if (reserva.getAsientosReservados() != null && !reserva.getAsientosReservados().isEmpty()) {
            detalles.append("Asientos de Vuelo Reservados:\n");
            for (Asiento asiento : reserva.getAsientosReservados()) {
                detalles.append("  - Vuelo: ").append(asiento.getVuelo().getNombre()).append("\n");
                detalles.append("    Asiento: ").append(asiento.getNumero()).append("\n");
            }
        }

        return detalles.toString();
    }
}