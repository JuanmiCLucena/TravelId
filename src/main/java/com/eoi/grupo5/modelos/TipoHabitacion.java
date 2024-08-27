package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un tipo de habitación en el sistema.
 * <p>
 * Esta entidad clasifica las habitaciones en distintos tipos, proporcionando un nombre y
 * una descripción opcional. Cada tipo de habitación puede estar asociado con múltiples
 * habitaciones en el sistema.
 * </p>
 */
@Entity
@Table(name = "tipoHabitacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 45, nullable = false)
    @NotNull(message = "El nombre no puede ser nulo")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name = "descripcion", length = 150)
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    private String descripcion;

    /**
     * Conjunto de habitaciones asociadas a este tipo de habitación.
     * <p>
     * Define la relación uno a muchos con la entidad {@link Habitacion}, permitiendo la
     * obtención de todas las habitaciones que pertenecen a este tipo.
     * </p>
     */
    @OneToMany(mappedBy = "tipo")
    private Set<Habitacion> habitaciones = new HashSet<>();
}
