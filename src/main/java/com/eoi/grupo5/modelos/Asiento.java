package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un asiento en un vuelo.
 * Incluye detalles como el número de asiento, la categoría, y las relaciones con reservas, vuelos y precios.
 */
@Entity
@Table(name = "asientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "numero", length = 5)
    @NotBlank(message = "El asiento debe tener un número asignado")
    @Size(max = 5, message = "El número de asiento no puede tener más de 5 caracteres")
    private String numero;

    /**
     * Relación Many-to-One con la entidad {@link CategoriaAsiento}.
     * Define la categoría a la que pertenece el asiento (por ejemplo, clase económica, business, etc.).
     */
    @ManyToOne
    @JoinColumn(name = "idCategoria", foreignKey = @ForeignKey(name = "fkAsiCat"), nullable = false)
    @NotNull(message = "Debe asignarse una categoría al asiento")
    private CategoriaAsiento categoria;

    /**
     * Relación Many-to-Many con la entidad {@link Reserva}.
     * Un asiento puede estar asociado a múltiples reservas, ya que puede ser reservado en diferentes vuelos.
     */
    @ManyToMany(mappedBy = "asientosReservados")
    private Set<Reserva> reservas = new HashSet<>();

    /**
     * Relación Many-to-One con la entidad {@link Vuelo}.
     * Indica a qué vuelo está asignado el asiento.
     */
    @ManyToOne
    @JoinColumn(name = "idVuelo", foreignKey = @ForeignKey(name = "fkAsiVuelos"), nullable = false)
    @NotNull(message = "El asiento debe estar asignado a un vuelo")
    private Vuelo vuelo;

    /**
     * Relación One-to-Many con la entidad {@link Precio}.
     * Un asiento puede tener múltiples precios asociados, dependiendo de las circunstancias (por ejemplo, tarifas diferentes).
     */
    @OneToMany(mappedBy = "asiento")
    private Set<Precio> precio = new HashSet<>();
}
