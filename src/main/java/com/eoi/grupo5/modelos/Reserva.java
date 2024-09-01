package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa una reserva realizada por un usuario en el sistema.
 * Incluye la información sobre las fechas de inicio y fin de la reserva,
 * su estado de cancelación, y las entidades asociadas como usuarios,
 * asientos, habitaciones y pagos.
 */
@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaInicio")
    @NotNull(message = "Debes introducir una fecha de inicio de reserva")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaFin")
    @NotNull(message = "Debes introducir una fecha de fin de reserva")
    private LocalDateTime fechaFin;

    @Basic(optional = false)
    @Column(name = "cancelado")
    private boolean cancelado = false;

    /**
     * Relación Many-to-One con la entidad {@link Usuario}.
     * Indica el usuario que realizó la reserva.
     */
    @ManyToOne
    @JoinColumn(name = "idUsuario", foreignKey = @ForeignKey(name = "fkReservasUsuarios"), nullable = false)
    @NotNull(message = "Debe haber un usuario asociado a la reserva")
    private Usuario usu;

    /**
     * Relación Many-to-Many con la entidad {@link Asiento}.
     * Los asientos reservados en esta reserva.
     */
    @ManyToMany
    @JoinTable(
            name = "asientosReservados",
            joinColumns = @JoinColumn(name = "idReserva", foreignKey = @ForeignKey(name = "fkAsresReservas")),
            inverseJoinColumns = @JoinColumn(name = "idAsiento", foreignKey = @ForeignKey(name = "fkAsresAsientos"))
    )
    private Set<Asiento> asientosReservados = new HashSet<>();

    /**
     * Relación Many-to-Many con la entidad {@link Habitacion}.
     * Las habitaciones reservadas en esta reserva.
     */
    @ManyToMany
    @JoinTable(
            name = "habitacionesReservadas",
            joinColumns = @JoinColumn(name = "idReserva", foreignKey = @ForeignKey(name = "fkHabresReservas")),
            inverseJoinColumns = @JoinColumn(name = "idHabitacion", foreignKey = @ForeignKey(name = "fkHabresHabitaciones"))
    )
    private Set<Habitacion> habitacionesReservadas = new HashSet<>();

    /**
     * Relación One-to-Many con la entidad {@link ReservaActividad}.
     * Las actividades asociadas a esta reserva.
     */
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservaActividad> reservaActividades = new HashSet<>();

    /**
     * Relación One-to-One con la entidad {@link Pago}.
     * Información sobre el pago realizado para esta reserva.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "idPago", referencedColumnName = "id")
    private Pago pago;

}
