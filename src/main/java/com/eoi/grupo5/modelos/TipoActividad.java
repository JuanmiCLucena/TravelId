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
 * Representa un tipo de actividad en el sistema.
 * <p>
 * Esta entidad se utiliza para categorizar las actividades, proporcionando un nombre y una
 * descripción opcional que ayudan en la organización y clasificación de las actividades en
 * el sistema. Cada tipo de actividad puede tener múltiples actividades asociadas.
 * </p>
 */

@Entity
@Table(name = "tipoActividad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoActividad {
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
     * Conjunto de actividades asociadas a este tipo de actividad.
     * <p>
     * Esta relación define las actividades que pertenecen a este tipo. Permite la obtención
     * de todas las actividades vinculadas con el tipo de actividad específico.
     * </p>
     */
    @OneToMany(mappedBy = "tipo")
    private Set<Actividad> actividades = new HashSet<>();

}
