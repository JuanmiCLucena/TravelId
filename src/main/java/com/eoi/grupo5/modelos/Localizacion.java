package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
    @NotNull(message = "La localización debe tener un nombre")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name = "codigo", length = 10)
    @NotNull(message = "La localización debe tener un código")
    @Size(max = 10, message = "El código no puede tener más de 10 caracteres")
    private String codigo;

    @OneToMany(mappedBy = "destino")
    private Set<Vuelo> vuelosOrigen = new HashSet<>();

    @OneToMany(mappedBy = "destino")
    private Set<Vuelo> vuelosDestino = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idPais", foreignKey = @ForeignKey(name = "fkLocalPaises"), nullable = false)
    @NotNull(message = "La localización debe tener un país asociado")
    private Pais pais;

    @OneToMany(mappedBy = "localizacion")
    private Set<Hotel> hoteles = new HashSet<>();

    @OneToMany(mappedBy = "localizacion")
    private Set<Actividad> actividades = new HashSet<>();

}
