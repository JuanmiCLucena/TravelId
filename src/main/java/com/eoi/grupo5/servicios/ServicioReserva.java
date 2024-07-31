package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaReservas;
import com.eoi.grupo5.repos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioReserva extends AbstractBusinessServiceSoloEnt<Reserva, Integer, RepoReserva>{

    private final RepoReserva repoReserva;
    private final RepoHabitacion repoHabitacion;
    private final RepoAsiento repoAsiento;
    private final RepoActividad repoActividad;
    private final RepoPago repoPago;
    private final RepoMetodoPago repoMetodoPago;

    protected ServicioReserva(RepoReserva repoReserva, RepoHabitacion repoHabitacion, RepoAsiento repoAsiento, RepoActividad repoActividad, RepoPago repoPago, RepoMetodoPago repoMetodoPago) {
        super(repoReserva);
        this.repoReserva = repoReserva;
        this.repoHabitacion = repoHabitacion;
        this.repoAsiento = repoAsiento;
        this.repoActividad = repoActividad;
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

    public void addHabitacion(Integer reservaId, Integer idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Optional<Reserva> optionalReserva = repoReserva.findById(reservaId);
        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();
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
        } else {
            throw new RuntimeException("No se encontró la reserva.");
        }
    }

    public void añadirActividad(Integer reservaId, Integer idActividad, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Optional<Reserva> optionalReserva = repoReserva.findById(reservaId);
        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();
            Optional<Actividad> optionalActividad = repoActividad.findById(idActividad);
            if (optionalActividad.isPresent()) {
                Actividad actividad = optionalActividad.get();
                if (actividad.getFechaInicio().isBefore(fechaFin) && actividad.getFechaFin().isAfter(fechaInicio)) {
                    if (actividad.getAsistentesConfirmados() < actividad.getMaximosAsistentes()) {
                        reserva.getActividadesReservadas().add(actividad);
                        actividad.setAsistentesConfirmados(actividad.getAsistentesConfirmados() + 1);
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
        } else {
            throw new RuntimeException("No se encontró la reserva.");
        }
    }

    public void añadirAsiento(Integer reservaId, Integer idAsiento) {
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

    public void confirmarReserva(Integer reservaId) {
        Optional<Reserva> optionalReserva = repoReserva.findById(reservaId);
        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();
            reserva.setCancelado(false);
            repoReserva.save(reserva);
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

    public void generarPago(Integer reservaId, Double precioTotal, Integer metodoPagoId) {
        Optional<Reserva> optionalReserva = repoReserva.findById(reservaId);
        if (optionalReserva.isPresent()) {
            Reserva reserva = optionalReserva.get();
            Optional<MetodoPago> optionalMetodoPago = repoMetodoPago.findById(metodoPagoId);
            if (optionalMetodoPago.isPresent()) {
                MetodoPago metodoPago = optionalMetodoPago.get();
                Pago pago = new Pago();
                pago.setImporte(precioTotal);
                pago.setFechaPago(LocalDateTime.now());
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

}
