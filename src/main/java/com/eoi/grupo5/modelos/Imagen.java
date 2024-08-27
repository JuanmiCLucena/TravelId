package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una imagen en el sistema.
 * Incluye información sobre la URL de la imagen y las entidades a las que puede estar asociada: hotel, actividad, habitación o vuelo.
 */
@Entity
@Table(name = "imagenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url", nullable = false)
    @NotNull(message = "La URL no puede ser nula")
    @Size(max = 300, message = "La URL no puede tener más de 300 caracteres")
    private String url;

    /**
     * Relación Many-to-One con la entidad `Hotel`.
     * Esta relación indica que la imagen puede estar asociada a un hotel.
     * Puede ser `null` si la imagen no está asociada a un hotel.
     */
    @ManyToOne
    @JoinColumn(name = "idHotel")
    private Hotel hotel;

    /**
     * Relación Many-to-One con la entidad {@link Actividad}.
     * Esta relación indica que la imagen puede estar asociada a una actividad.
     * Puede ser `null` si la imagen no está asociada a una actividad.
     */
    @ManyToOne
    @JoinColumn(name = "idActividad")
    private Actividad actividad;

    /**
     * Relación Many-to-One con la entidad {@link Habitacion}.
     * Esta relación indica que la imagen puede estar asociada a una habitación.
     * Puede ser `null` si la imagen no está asociada a una habitación.
     */
    @ManyToOne
    @JoinColumn(name = "idHabitacion")
    private Habitacion habitacionImagen;

    /**
     * Relación One-to-One con la entidad {@link Vuelo}.
     * Esta relación indica que la imagen puede estar asociada a un vuelo.
     * Cada vuelo puede tener una imagen asociada que se define en la entidad `Vuelo`.
     */
    @OneToOne(mappedBy = "imagen")
    private Vuelo vuelo;
}
