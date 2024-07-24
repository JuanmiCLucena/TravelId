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
@Table(name = "paises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 45, nullable = false)
    @NotNull(message = "El pais debe tener un nombre asociado")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @OneToMany(mappedBy="pais")
    private Set<Localizacion> localizaciones = new HashSet<>();

}
