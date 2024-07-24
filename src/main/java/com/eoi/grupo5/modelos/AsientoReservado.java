package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "asientosReservados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsientoReservado {

    @EmbeddedId
    private AsientoReservadoId id;

    @ManyToOne
    @MapsId("idAsiento")
    @JoinColumn(name = "idAsiento")
    @NotNull(message = "Debes introducir un asiento")
    private Asiento asiento;

    @ManyToOne
    @MapsId("idReserva")
    @JoinColumn(name = "idReserva")
    @NotNull(message = "Debes asignar una reserva")
    private Reserva reserva;

    @Column(name = "fechaVuelo")
    @NotNull(message = "Debes introducir una fecha de vuelo")
    @FutureOrPresent(message = "La fecha de vuelo debe ser en el presente o en el futuro")
    private LocalDateTime fechaVuelo;

    @Column(name = "horaVuelo")
    @NotNull(message = "Debes introducir una hora de vuelo")
    @FutureOrPresent(message = "La hora de vuelo debe ser en el presente o en el futuro")
    private LocalDateTime horaVuelo;

}
