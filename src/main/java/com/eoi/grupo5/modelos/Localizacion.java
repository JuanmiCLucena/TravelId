package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa una localización en el sistema.
 * Incluye información sobre el nombre y código de la localización,
 * así como las entidades asociadas: vuelos, hoteles y actividades.
 */
@Entity
@Table(name = "localizaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Localizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 45)
    @NotBlank(message = "La localización debe tener un nombre")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name = "codigo", length = 10)
    @NotBlank(message = "La localización debe tener un código")
    @Size(max = 10, message = "El código no puede tener más de 10 caracteres")
    private String codigo;

    /**
     * Relación One-to-Many con la entidad {@link Vuelo}.
     * Representa los vuelos que tienen esta localización como destino.
     */
    @OneToMany(mappedBy = "destino")
    private Set<Vuelo> vuelosDestino = new HashSet<>();

    /**
     * Relación One-to-Many con la entidad {@link Vuelo}.
     * Representa los vuelos que tienen esta localización como origen.
     */
    @OneToMany(mappedBy = "origen")
    private Set<Vuelo> vuelosOrigen = new HashSet<>();

    /**
     * Relación Many-to-One con la entidad {@link Pais}.
     * Cada localización debe estar asociada a un país específico.
     */
    @ManyToOne
    @JoinColumn(name = "idPais", foreignKey = @ForeignKey(name = "fkLocalPaises"), nullable = false)
    @NotNull(message = "La localización debe tener un país asociado")
    private Pais pais;

    /**
     * Relación One-to-Many con la entidad {@link Hotel}.
     * Representa los hoteles ubicados en esta localización.
     */
    @OneToMany(mappedBy = "localizacion")
    private Set<Hotel> hoteles = new HashSet<>();

    /**
     * Relación One-to-Many con la entidad {@link Actividad}.
     * Representa las actividades que se llevan a cabo en esta localización.
     */
    @OneToMany(mappedBy = "localizacion")
    private Set<Actividad> actividades = new HashSet<>();
}
