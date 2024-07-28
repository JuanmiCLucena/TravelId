package com.eoi.grupo5.modelos;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "idHotel")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "idActividad")
    private Actividad actividad;

    @ManyToOne
    @JoinColumn(name = "idHabitacion")
    private Habitacion habitacionImagen;

    @OneToOne(mappedBy = "imagen")
    private Vuelo vuelo;
}

