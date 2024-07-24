package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detallesUsuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetallesUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, length = 50)
    @NotNull(message = "Introduce un Correo electrónico")
    @Email(message = "El correo debe ser válido")
    @Size(min = 5, max = 50, message = "El correo debe tener entre 5 y 50 caracteres")
    private String email;

    @Column(name = "dni", length = 10)
    @Pattern(regexp = "\\d{8}[A-Za-z]", message = "El DNI debe tener 8 dígitos seguidos de una letra")
    private String dni;

    @Column(name = "edad")
    @Min(value = 18, message = "Debes tener al menos 18 años")
    private Integer edad;

    @Column(name = "telefono", length = 15)
    @Pattern(regexp = "\\d{9,15}", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String telefono;

    @Column(name="nombre", length = 45)
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name="apellidos", length = 45)
    @Size(max = 45, message = "Los apellidos no pueden tener más de 45 caracteres")
    private String apellidos;

    @OneToOne
    @MapsId
    @JoinColumn(name = "idUsuario")
    private Usuario usu;

    // Constructores
    public DetallesUsuario(String email) {
        this.email = email;
    }
}
