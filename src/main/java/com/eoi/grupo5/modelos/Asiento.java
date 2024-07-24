package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "asientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "numero", length = 5)
    @NotNull(message = "El asiento debe tener un número asignado")
    @Size(max = 5, message = "El número de asiento no puede tener más de 5 caracteres")
    private String numero;

    @ManyToOne
    @JoinColumn(name = "idCategoria", foreignKey = @ForeignKey(name = "fkAsiCat"), nullable = false)
    @NotNull(message = "Debe asignarse una categoría al asiento")
    private CategoriaAsiento categoria;

    @OneToMany(mappedBy = "asiento")
    private Set<AsientoReservado> asientosReservados = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idVuelo", foreignKey = @ForeignKey(name = "fkAsiVuelos"), nullable = false)
    @NotNull(message = "El asiento debe estar asignado a un vuelo")
    private Vuelo vuelo;

    @OneToMany(mappedBy = "asiento")
    private List<Precio> precio;

}
