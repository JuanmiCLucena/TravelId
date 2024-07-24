package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name="numero", nullable = false)
    @NotNull(message = "La habitación debe tener un número")
    @Min(value = 1, message = "El número de habitación debe ser un valor positivo")
    private Integer numero;

    @Column(name="capacidad")
    @NotNull(message = "La capacidad no puede ser nula")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Byte capacidad;

    @Column(name = "numeroCamas")
    @NotNull(message = "El número de camas no puede ser nulo")
    @Min(value = 1, message = "El número de camas debe ser al menos 1")
    private Byte numeroCamas;

    @ManyToOne
    @JoinColumn(name = "idTipo", foreignKey = @ForeignKey(name = "fkHabitacionesTipo"), nullable = false)
    @NotNull(message = "La habitación debe tener un tipo asociado")
    private TipoHabitacion tipo;

    @ManyToOne
    @JoinColumn(name = "idHotel", foreignKey = @ForeignKey(name = "fkHabitacionesHoteles"), nullable = false)
    @NotNull(message = "La habitación debe tener un Hotel asociado")
    private Hotel hotel;

    @OneToMany(mappedBy = "habitacion")
    private Set<HabitacionReservada> habitacionesReservadas = new HashSet<>();

    @OneToMany(mappedBy = "habitacionImagen",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Imagen> imagenesHabitacion = new HashSet<>();

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Precio> precio = new HashSet<>();

}
