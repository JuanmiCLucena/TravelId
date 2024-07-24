package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "habitacionesReservadas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionReservada {

    @EmbeddedId
    private HabitacionReservadaId id;

    @ManyToOne
    @MapsId("idHabitacion")
    @JoinColumn(name = "idHabitacion")
    @NotNull(message = "La habitación no puede ser nula")
    private Habitacion habitacion;

    @ManyToOne
    @MapsId("idReserva")
    @JoinColumn(name = "idReserva")
    @NotNull(message = "La reserva no puede ser nula")
    private Reserva reserva;

    @Column(name = "FechaInicio", nullable = false)
    @NotNull(message = "Debes introducir una fecha de inicio de reserva")
    @FutureOrPresent(message = "La fecha de Inicio debe ser en el presente o en el futuro")
    private LocalDateTime fechaInicio;

    @Column(name = "FechaFin", nullable = false)
    @NotNull(message = "Debes introducir una fecha de fin de reserva")
    @Future(message = "La fecha de fin debe ser en el futuro")
    private LocalDateTime fechaFin;


}
