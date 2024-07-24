package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "idHabitacion")
    private Habitacion habitacion;

    @ManyToOne
    @JoinColumn(name = "idAsiento")
    private Asiento asiento;

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