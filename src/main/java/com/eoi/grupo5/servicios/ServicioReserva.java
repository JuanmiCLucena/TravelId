package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaReservas;
import com.eoi.grupo5.repos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ServicioReserva extends AbstractBusinessServiceSoloEnt<Reserva, Integer, RepoReserva>{

    private final RepoReserva repoReserva;
    private final RepoHabitacion repoHabitacion;
    private final RepoAsiento repoAsiento;
    private final RepoActividad repoActividad;
    private final RepoReservaActividad repoReservaActividad;
    private final RepoPago repoPago;
    private final RepoMetodoPago repoMetodoPago;

    protected ServicioReserva(RepoReserva repoReserva, RepoHabitacion repoHabitacion, RepoAsiento repoAsiento, RepoActividad repoActividad, RepoReservaActividad repoReservaActividad, RepoPago repoPago, RepoMetodoPago repoMetodoPago) {
        super(repoReserva);
        this.repoReserva = repoReserva;
        this.repoHabitacion = repoHabitacion;
        this.repoAsiento = repoAsiento;
        this.repoActividad = repoActividad;
        this.repoReservaActividad = repoReservaActividad;
        this.repoPago = repoPago;
        this.repoMetodoPago = repoMetodoPago;
    }

    public Reserva crearReserva(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Reserva reserva = new Reserva();
        reserva.setUsu(usuario);
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        return repoReserva.save(reserva);
    }

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

    public void addActividad(Reserva reserva, Integer idActividad, LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer asistentes) {
        Optional<Actividad> optionalActividad = repoActividad.findById(idActividad);
        if (optionalActividad.isPresent()) {
            Actividad actividad = optionalActividad.get();
            if (actividad.getFechaInicio().isBefore(fechaFin) && actividad.getFechaFin().isAfter(fechaInicio)) {
                if ((actividad.getAsistentesConfirmados() + asistentes) <= actividad.getMaximosAsistentes()) {
                    // Buscar si ya existe una reserva de la actividad
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

    public void generarPago(Reserva reserva, Double precioTotal, Integer metodoPagoId) {
        Optional<MetodoPago> optionalMetodoPago = repoMetodoPago.findById(metodoPagoId);
        if (optionalMetodoPago.isPresent()) {
            MetodoPago metodoPago = optionalMetodoPago.get();
            Pago pago = new Pago();
            pago.setImporte(precioTotal);
            pago.setFechaPago(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            pago.setMetodoPago(metodoPago);
            pago.setReserva(reserva);

            // Asociar el pago a la reserva
            reserva.setPago(pago);

            // Guardar el pago
            repoPago.save(pago);

            // Guardar la reserva actualizada
            repoReserva.save(reserva);
        } else {
            throw new RuntimeException("No se encontró el método de pago.");
        }
    }

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

    public List<Reserva> obtenerReservasPorUsuario(Usuario usuario) {
        return repoReserva.findByUsu(usuario);
    }

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
