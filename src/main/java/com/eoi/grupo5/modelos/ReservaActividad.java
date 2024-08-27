package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa la asociación entre una reserva y una actividad, incluyendo
 * el número de asistentes para dicha actividad en el contexto de la reserva.
 */
@Entity
@Table(name = "reservas_actividades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Relación Many-to-One con la entidad {@link Reserva}.
     * Indica la reserva asociada a esta actividad.
     */
    @ManyToOne
    @JoinColumn(name = "idReserva", foreignKey = @ForeignKey(name = "fkReservaActividadReserva"), nullable = false)
    private Reserva reserva;

    /**
     * Relación Many-to-One con la entidad {@link Actividad}.
     * Indica la actividad asociada a esta reserva.
     */
    @ManyToOne
    @JoinColumn(name = "idActividad", foreignKey = @ForeignKey(name = "fkReservaActividadActividad"), nullable = false)
    private Actividad actividad;

    /**
     * Número de asistentes para la actividad en el contexto de esta reserva.
     */
    @Column(name = "asistentes")
    @NotNull(message = "Debes introducir el número de asistentes")
    private Integer asistentes;
}
