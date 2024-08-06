package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.validacionesCustom.anotaciones.DniPattern;
import com.eoi.grupo5.validacionesCustom.anotaciones.TelefonoPattern;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombreUsuario", nullable = false, length = 45)
    @NotBlank(message = "Debes introducir un nombre de usuario")
    @Size(min = 2, max = 45, message = "El nombre de usuario debe tener entre 2 y 45 caracteres")
    private String nombreUsuario;

    @Column(name = "password", nullable = false, length = 150)
    @NotBlank(message = "La contraseña no puede ser nula")
    @Size(min = 5, max = 150, message = "La contraseña debe tener entre 5 y 150 caracteres")
    private String password;

    @Column(name = "email", nullable = false, length = 50)
    @NotBlank(message = "Introduce un Correo electrónico")
    @Email(message = "El correo debe ser válido")
    @Size(min = 5, max = 50, message = "El correo debe tener entre 5 y 50 caracteres")
    private String email;

    @Column(name = "dni", length = 10)
    @DniPattern
    private String dni;

    @Column(name = "edad")
    @Min(value = 18, message = "Debes tener al menos 18 años")
    private Integer edad;

    @Column(name = "telefono", length = 15)
    @TelefonoPattern
    private String telefono;

    @Column(name="nombre", length = 45)
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Column(name="apellidos", length = 45)
    @Size(max = 45, message = "Los apellidos no pueden tener más de 45 caracteres")
    private String apellidos;


    @Basic(optional = false)
    private boolean active = true;

    //Helpers
    public static UsuarioRegistroDto from(Usuario usuario) {
        UsuarioRegistroDto usuarioRegistroDto = new UsuarioRegistroDto();
        usuarioRegistroDto.setId(usuario.getId());
        usuarioRegistroDto.setNombreUsuario(usuario.getNombreUsuario());
        usuarioRegistroDto.setPassword(usuario.getPassword());
        usuarioRegistroDto.setEmail(usuario.getDetalles().getEmail());
        usuarioRegistroDto.setDni(usuario.getDetalles().getDni());
        usuarioRegistroDto.setEdad(usuario.getDetalles().getEdad());
        usuarioRegistroDto.setNombre(usuario.getDetalles().getNombre());
        usuarioRegistroDto.setApellidos(usuario.getDetalles().getApellidos());
        usuarioRegistroDto.setEmail(usuario.getDetalles().getEmail());
        usuarioRegistroDto.setTelefono(usuario.getDetalles().getTelefono());

        return usuarioRegistroDto;
    }

    /**
     * Convierte una cadena vacía a {@code null}.
     *
     * @param value La cadena que se va a convertir.
     * @return {@code null} si la cadena es vacía o solo contiene espacios; de lo contrario, devuelve la cadena original.
     */
    public static String convertEmptyToNull(String value) {
        return (value != null && value.trim().isEmpty()) ? null : value;
    }

    /**
     * Sanitiza los campos del DTO, convirtiendo las cadenas vacías en {@code null}.
     *
     * Este método se utiliza para asegurarse de que los campos que llegan vacíos desde un formulario
     * se conviertan en {@code null} en lugar de quedarse como cadenas vacías.
     * Esto evitará posibles errores de validación
     */
    public void sanitize() {
        this.nombreUsuario = convertEmptyToNull(this.nombreUsuario);
        this.password = convertEmptyToNull(this.password);
        this.email = convertEmptyToNull(this.email);
        this.dni = convertEmptyToNull(this.dni);
        this.telefono = convertEmptyToNull(this.telefono);
        this.nombre = convertEmptyToNull(this.nombre);
        this.apellidos = convertEmptyToNull(this.apellidos);
    }


}
