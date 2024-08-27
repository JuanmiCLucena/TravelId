package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa un hotel en el sistema.
 * Incluye información sobre el nombre, categoría, descripción, contacto, localización y las habitaciones e imágenes asociadas al hotel.
 */
@Entity
@Table(name = "hoteles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 45, nullable = false)
    @NotBlank(message = "El Hotel debe tener un nombre")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name = "categoria")
    @NotNull(message = "El Hotel debe tener una categoría")
    @Min(value = 1, message = "La categoría debe ser al menos 1")
    @Max(value = 5, message = "La categoría no puede ser mayor a 5")
    private Byte categoria;

    @Column(name = "descripcion", length = 150)
    @NotBlank(message = "El Hotel debe tener una descripción")
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    private String descripcion;

    @Column(name = "contacto", length = 45)
    @Size(max = 45, message = "El contacto no puede tener más de 45 caracteres")
    private String contacto;

    /**
     * Relación One-to-Many con la entidad {@link Habitacion}.
     * Cada hotel puede tener múltiples habitaciones.
     * La relación se gestiona con eliminación en cascada y eliminación de huérfanos.
     */
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Habitacion> habitaciones = new HashSet<>();

    /**
     * Relación Many-to-One con la entidad {@link Localizacion}.
     * Cada hotel debe estar asociado a una localización específica.
     */
    @ManyToOne
    @JoinColumn(name = "idLocalizacion", foreignKey = @ForeignKey(name = "fkHotelesLocal"))
    @NotNull(message = "El hotel debe tener una localización")
    private Localizacion localizacion;

    /**
     * Relación One-to-Many con la entidad {@link Imagen}.
     * Cada hotel puede tener múltiples imágenes asociadas.
     * La relación se gestiona con eliminación en cascada y eliminación de huérfanos.
     */
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Imagen> imagenesHotel = new HashSet<>();

    /**
     * Obtiene la URL de la primera imagen asociada al hotel.
     *
     * <p>Este método busca la imagen con el menor identificador (ID) entre las imágenes asociadas al hotel
     * y devuelve su URL. Si no hay imágenes asociadas, devuelve `null`.</p>
     *
     * <p>Actualmente, este método no tiene uso en el código, pero puede ser útil para mostrar una imagen representativa
     * del hotel en futuras implementaciones o funcionalidades.</p>
     *
     * @return URL de la imagen o `null` si no hay imágenes asociadas.
     */
    public String getPrimeraImagenUrl() {
        return imagenesHotel.stream()
                .min(Comparator.comparing(Imagen::getId))
                .map(Imagen::getUrl)
                .orElse(null);
    }
}
