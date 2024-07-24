package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

    @Column(name = "nombre", length = 45)
    @NotNull(message = "Introduce un nombre de Actividad")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name = "descripcion", length = 150)
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaInicio")
    @NotNull(message = "Debes introducir una fecha de inicio")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaFin")
    @NotNull(message = "Debes introducir una fecha de fin")
    @Future(message = "La fecha de fin debe ser posterior a la fecha de Inicio")
    private LocalDateTime fechaFin;

    @ManyToOne
    @JoinColumn(name = "idTipo", foreignKey = @ForeignKey(name = "fkActiTipo"), nullable = false)
    @NotNull(message = "La actividad debe tener un tipo")
    private TipoActividad tipo;

    @ManyToMany(mappedBy = "actividades")
    private Set<Reserva> reservas = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idLocalizacion", foreignKey = @ForeignKey(name = "fkActividadesLocal"), nullable = false)
    @NotNull(message = "Debes introducir una Localización")
    private Localizacion localizacion;

    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Imagen> imagenes = new HashSet<>();

    @OneToMany(mappedBy = "actividad")
    private Set<Precio> precio = new HashSet<>();


}
