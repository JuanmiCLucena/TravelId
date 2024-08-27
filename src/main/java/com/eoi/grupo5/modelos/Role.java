package com.eoi.grupo5.modelos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa un rol en el sistema. Cada rol tiene un nombre y puede ser asociado a varios usuarios.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotEmpty(message = "El nombre del rol no puede estar vacío")
    @Size(max = 45, message = "El nombre del rol no puede tener más de 45 caracteres")
    private String roleName;

    /**
     * Relación Many-to-Many con la entidad {@link Usuario}.
     * Se usa `FetchType.EAGER` para cargar los usuarios de inmediato.
     * La referencia inversa se maneja con `@JsonBackReference` para evitar bucles infinitos en la serialización.
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    @JsonBackReference
    private Set<Usuario> usuarios = new HashSet<>();

}
