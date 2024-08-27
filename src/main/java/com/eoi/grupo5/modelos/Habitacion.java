package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa una habitación en un hotel.
 * Incluye información sobre el número de la habitación, su capacidad, tipo, hotel asociado y relaciones con reservas, imágenes y precios.
 */
@Entity
@Table(name = "habitaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "numero", nullable = false)
    @NotNull(message = "La habitación debe tener un número")
    @Min(value = 1, message = "El número de habitación debe ser un valor positivo")
    private Integer numero;

    @Column(name = "capacidad")
    @NotNull(message = "La capacidad no puede ser nula")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Byte capacidad;

    @Column(name = "numeroCamas")
    @NotNull(message = "El número de camas no puede ser nulo")
    @Min(value = 1, message = "El número de camas debe ser al menos 1")
    private Byte numeroCamas;

    /**
     * Relación Many-to-One con la entidad {@link TipoHabitacion}.
     * Cada habitación debe estar asociada a un tipo de habitación, como estándar, suite, etc.
     */
    @ManyToOne
    @JoinColumn(name = "idTipo", foreignKey = @ForeignKey(name = "fkHabitacionesTipo"), nullable = false)
    @NotNull(message = "La habitación debe tener un tipo asociado")
    private TipoHabitacion tipo;

    /**
     * Relación Many-to-One con la entidad {@link Hotel}.
     * Cada habitación está asociada a un hotel específico.
     */
    @ManyToOne
    @JoinColumn(name = "idHotel", foreignKey = @ForeignKey(name = "fkHabitacionesHoteles"), nullable = false)
    @NotNull(message = "La habitación debe tener un Hotel asociado")
    private Hotel hotel;

    /**
     * Relación Many-to-Many con la entidad {@link Reserva}.
     * Una habitación puede estar asociada a múltiples reservas a lo largo del tiempo.
     */
    @ManyToMany(mappedBy = "habitacionesReservadas")
    private Set<Reserva> reservas = new HashSet<>();

    /**
     * Relación One-to-Many con la entidad {@link Imagen}.
     * Cada habitación puede tener múltiples imágenes asociadas.
     * La relación se gestiona con eliminación en cascada y eliminación de huérfanos.
     */
    @OneToMany(mappedBy = "habitacionImagen", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Imagen> imagenesHabitacion = new HashSet<>();

    /**
     * Relación One-to-Many con la entidad {@link Precio}.
     * Cada habitación puede tener múltiples precios asociados, dependiendo de las tarifas o promociones.
     * La relación se gestiona con eliminación en cascada y eliminación de huérfanos.
     */
    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Precio> precio = new HashSet<>();

    /**
     * Obtiene la URL de la primera imagen asociada a la habitación.
     *
     * <p>Este método busca la imagen con el menor identificador (ID) entre las imágenes asociadas a la habitación
     * y devuelve su URL. Si no hay imágenes asociadas, devuelve `null`.</p>
     *
     * <p>Actualmente, este método no tiene uso en el código, pero puede ser útil para obtener una representación
     * visual de la habitación en futuras implementaciones o funcionalidades.</p>
     *
     * @return URL de la imagen o `null` si no hay imágenes asociadas.
     */
    public String getPrimeraImagenUrl() {
        return imagenesHabitacion.stream()
                .min(Comparator.comparing(Imagen::getId))
                .map(Imagen::getUrl)
                .orElse(null);
    }
}
