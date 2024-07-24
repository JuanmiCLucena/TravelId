package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ServicioReserva extends AbstractBusinessServiceSoloEnt<Reserva, Integer, RepoReserva>{

    private final RepoReserva repoReserva;
    private final RepoHabitacion repoHabitacion;
    private final RepoHabitacionReservada repoHabitacionReservada;
    private final RepoAsiento repoAsiento;
    private final RepoAsientoReservado repoAsientoReservado;
    private final RepoActividad repoActividad;

    protected ServicioReserva(RepoReserva repoReserva, RepoHabitacion repoHabitacion, RepoHabitacionReservada repoHabitacionReservada, RepoVuelo repoVuelo, RepoAsiento repoAsiento, RepoAsientoReservado repoAsientoReservado, RepoActividad repoActividad) {
        super(repoReserva);
        this.repoReserva = repoReserva;
        this.repoHabitacion = repoHabitacion;
        this.repoHabitacionReservada = repoHabitacionReservada;
        this.repoAsiento = repoAsiento;
        this.repoAsientoReservado = repoAsientoReservado;
        this.repoActividad = repoActividad;
    }

    public Reserva crearReserva(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Reserva reserva = new Reserva();
        reserva.setUsu(usuario);
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        return repoReserva.save(reserva);
    }

    public void addHabitacionToReserva(Reserva reserva, Integer idHabitacion, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Optional<Habitacion> optionalHabitacion = repoHabitacion.findById(idHabitacion);
        if (optionalHabitacion.isPresent()) {
            Habitacion habitacion = optionalHabitacion.get();
            HabitacionReservadaId habitacionReservadaId = new HabitacionReservadaId(habitacion.getId(), reserva.getId());
            HabitacionReservada habitacionReservada = new HabitacionReservada(habitacionReservadaId, habitacion, reserva, fechaInicio, fechaFin);
            reserva.getHabitacionesReservadas().add(habitacionReservada);
            repoReserva.save(reserva);
            repoHabitacionReservada.save(habitacionReservada);
        } else {
            throw new RuntimeException("No se encontró la habitación.");
        }
    }

    public void addAsientoToReserva(Reserva reserva, Integer idAsiento, LocalDateTime fechaVuelo, LocalDateTime horaVuelo) {
        Optional<Asiento> optionalAsiento = repoAsiento.findById(idAsiento);
        if (optionalAsiento.isPresent()) {
            Asiento asiento = optionalAsiento.get();
            AsientoReservadoId asientoReservadoId = new AsientoReservadoId(asiento.getId(), reserva.getId());
            AsientoReservado asientoReservado = new AsientoReservado(asientoReservadoId, asiento, reserva, fechaVuelo, horaVuelo);
            reserva.getAsientosReservados().add(asientoReservado);
            repoReserva.save(reserva);
            repoAsientoReservado.save(asientoReservado);
        } else {
            throw new RuntimeException("No se encontró el asiento.");
        }
    }
}
