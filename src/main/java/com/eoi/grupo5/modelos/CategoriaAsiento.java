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
 * Representa una categoría de asiento.
 * Incluye detalles como el nombre de la categoría y las relaciones con los asientos.
 */
@Entity
@Table(name = "categoriasAsiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 45)
    @NotNull(message = "Debes introducir un nombre de categoria")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    /**
     * Relación One-to-Many con la entidad {@link Asiento}.
     * Una categoría de asiento puede estar asociada a múltiples asientos.
     */
    @OneToMany(mappedBy = "categoria")
    private Set<Asiento> asientos = new HashSet<>();
}
