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
@Table(name = "companiasVuelo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompaniaVuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 45, nullable = false)
    @NotNull(message = "La compañia debe tener un nombre")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name = "contacto", length = 45)
    @Size(max = 45, message = "El contacto no puede tener más de 45 caracteres")
    private String contacto;

    @OneToMany(mappedBy = "compania")
    private Set<Vuelo> vuelos = new HashSet<>();

}
