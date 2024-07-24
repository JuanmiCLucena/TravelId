package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "metodosPago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetodosPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "metodo", length = 45, nullable = false)
    @NotNull(message = "Debes introducir un método de pago")
    @Size(max = 45, message = "El método de pago no puede tener más de 45 caracteres")
    private String metodo;

}
