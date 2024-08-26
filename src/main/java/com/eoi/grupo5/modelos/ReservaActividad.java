package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "idReserva", foreignKey = @ForeignKey(name = "fkReservaActividadReserva"), nullable = false)
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "idActividad", foreignKey = @ForeignKey(name = "fkReservaActividadActividad"), nullable = false)
    private Actividad actividad;

    @Column(name = "asistentes")
    @NotNull(message = "Debes introducir el número de asistentes")
    private Integer asistentes;
}
