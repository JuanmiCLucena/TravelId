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
 * Representa un país en el sistema.
 * Incluye el nombre del país y su relación con las localizaciones asociadas.
 */
@Entity
@Table(name = "paises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 45, nullable = false)
    @NotNull(message = "El país debe tener un nombre asociado")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    /**
     * Relación One-to-Many con la entidad {@link Localizacion}.
     * Cada país puede tener múltiples localizaciones asociadas.
     */
    @OneToMany(mappedBy = "pais")
    private Set<Localizacion> localizaciones = new HashSet<>();
}
