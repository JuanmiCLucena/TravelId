package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Representa el precio de un servicio o producto en el sistema.
 * Incluye el valor del precio, las fechas de vigencia y las relaciones con
 * habitaciones, asientos y actividades a las que se aplica el precio.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Precio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    @NotNull(message = "El valor no puede ser nulo")
    @Min(value = 0, message = "El valor debe ser mayor o igual a 0")
    private Double valor;

    /**
     * Relación Many-to-One con la entidad {@link Habitacion}.
     * El precio puede estar asociado a una habitación específica.
     */
    @ManyToOne
    @JoinColumn(name = "idHabitacion")
    private Habitacion habitacion;

    /**
     * Relación Many-to-One con la entidad {@link Asiento}.
     * El precio puede estar asociado a un asiento específico.
     */
    @ManyToOne
    @JoinColumn(name = "idAsiento")
    private Asiento asiento;

    /**
     * Relación Many-to-One con la entidad {@link Actividad}.
     * El precio puede estar asociado a una actividad específica.
     */
    @ManyToOne
    @JoinColumn(name = "idActividad")
    private Actividad actividad;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false)
    @NotNull(message = "La fecha de Inicio no puede ser nula")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false)
    @NotNull(message = "La fecha de fin no puede ser nula")
    private LocalDateTime fechaFin;
}
