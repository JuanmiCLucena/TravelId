package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
    @NotNull(message = "El Hotel debe tener un nombre")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name = "categoria")
    @Min(value = 1, message = "La categoría debe ser al menos 1")
    @Max(value = 5, message = "La categoría no puede ser mayor a 5")
    private Byte categoria;

    @Column(name = "descripcion", length = 150)
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    private String descripcion;

    @Column(name = "contacto", length = 45)
    @Size(max = 45, message = "El contacto no puede tener más de 45 caracteres")
    private String contacto;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Habitacion> habitaciones = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idLocalizacion", foreignKey = @ForeignKey(name = "fkHotelesLocal"))
    @NotNull(message = "El hotel debe tener una localización")
    private Localizacion localizacion;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Imagen> imagenesHotel = new HashSet<>();

}
