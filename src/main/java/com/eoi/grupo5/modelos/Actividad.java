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

    @ManyToOne
    @JoinColumn(name = "idTipo", foreignKey = @ForeignKey(name = "fkActiTipo"), nullable = false)
    @NotNull(message = "La actividad debe tener un tipo")
    private TipoActividad tipo;

    @ManyToOne
    @JoinColumn(name = "idLocalizacion", foreignKey = @ForeignKey(name = "fkActividadesLocal"), nullable = false)
    @NotNull(message = "Debes introducir una Localización")
    private Localizacion localizacion;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservaActividad> reservaActividades = new HashSet<>();

    @OneToMany(mappedBy = "actividad")
    private Set<Imagen> imagenes = new HashSet<>();

    @OneToMany(mappedBy = "actividad")
    private Set<Precio> precio = new HashSet<>();

    public String getPrimeraImagenUrl() {
        return imagenes.stream().min(Comparator.comparing(Imagen::getId))
                .map(Imagen::getUrl)
                .orElse(null);
    }
}
