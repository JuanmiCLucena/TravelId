package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa una actividad que puede realizar el usuario en una localización concreta.
 * Incluye detalles como nombre, descripción, fechas de inicio y fin, tipo, localización,
 * reservas asociadas, imágenes y precios.
 */
@Entity
@Table(name = "actividades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "maximosAsistentes")
    @NotNull(message = "Introduce la cantidad máxima de asistentes")
    private Integer maximosAsistentes;

    @Column(name = "asistentesConfirmados")
    @NotNull(message = "Introduce los asistentes confirmados")
    private Integer asistentesConfirmados;

    @Column(name = "nombre", length = 45)
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    @NotBlank(message = "Introduce un nombre de Actividad")
    private String nombre;

    @Column(name = "descripcion", length = 150)
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    @NotBlank(message = "Introduce una descripción")
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaInicio")
    @NotNull(message = "Debes introducir una fecha de inicio")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaFin")
    @NotNull(message = "Debes introducir una fecha de fin")
    private LocalDateTime fechaFin;

    /**
     * Relación Many-to-One con la entidad {@link TipoActividad}.
     * Cada actividad debe tener un tipo asociado.
     */
    @ManyToOne
    @JoinColumn(name = "idTipo", foreignKey = @ForeignKey(name = "fkActiTipo"), nullable = false)
    @NotNull(message = "La actividad debe tener un tipo")
    private TipoActividad tipo;

    /**
     * Relación Many-to-One con la entidad {@link Localizacion}.
     * Especifica la ubicación donde se llevará a cabo la actividad.
     */
    @ManyToOne
    @JoinColumn(name = "idLocalizacion", foreignKey = @ForeignKey(name = "fkActividadesLocal"), nullable = false)
    @NotNull(message = "Debes introducir una Localización")
    private Localizacion localizacion;

    /**
     * Relación One-to-Many con la entidad {@link ReservaActividad}.
     * Las reservas están asociadas a esta actividad y se eliminan si se elimina la actividad.
     */
    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservaActividad> reservaActividades = new HashSet<>();

    /**
     * Relación One-to-Many con la entidad {@link Imagen}.
     * Una actividad puede tener múltiples imágenes asociadas.
     */
    @OneToMany(mappedBy = "actividad")
    private Set<Imagen> imagenes = new HashSet<>();

    /**
     * Relación One-to-Many con la entidad {@link Precio}.
     * Una actividad puede tener múltiples precios asociados.
     */
    @OneToMany(mappedBy = "actividad")
    private Set<Precio> precio = new HashSet<>();

    /**
     * Obtiene la URL de la primera imagen asociada a la actividad.
     *
     * <p>Este método busca la imagen con el menor identificador (ID) entre las imágenes asociadas a la actividad
     * y devuelve su URL. Si no hay imágenes asociadas, devuelve {@code null}.</p>
     *
     * @return URL de la imagen o {@code null} si no hay imágenes asociadas.
     */
    public String getPrimeraImagenUrl() {
        return imagenes.stream()
                .min(Comparator.comparing(Imagen::getId))
                .map(Imagen::getUrl)
                .orElse(null);
    }
}
