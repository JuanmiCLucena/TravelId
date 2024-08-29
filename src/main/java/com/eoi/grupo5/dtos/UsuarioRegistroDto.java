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

/**
 * DTO para la gestión de usuarios en la administración y en el perfil del usuario.
 *
 * <p>Este DTO se utiliza para representar la información del usuario tanto en la administración de la aplicación
 * como en el perfil del usuario. Permite la captura y validación de datos del usuario, incluyendo detalles
 * personales y credenciales.</p>
 *
 * <p>Incorpora métodos para convertir cadenas vacías a {@code null} y sanitizar campos para asegurar
 * la validez de los datos antes de su procesamiento.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotBlank(message = "Debes introducir un nombre de usuario")
    @Size(min = 2, max = 45, message = "El nombre de usuario debe tener entre 2 y 45 caracteres")
    private String nombreUsuario;

    @Size(min = 5, max = 150, message = "La contraseña debe tener entre 5 y 150 caracteres")
    private String password;

    @NotBlank(message = "Introduce un Correo electrónico")
    @Email(message = "El correo debe ser válido")
    @Size(min = 5, max = 50, message = "El correo debe tener entre 5 y 50 caracteres")
    private String email;

    @DniPattern(message = "El DNI debe tener 8 números y 1 letra")
    private String dni;

    @Min(value = 18, message = "Debes tener al menos 18 años")
    private Integer edad;

    @TelefonoPattern
    private String telefono;

    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @Size(max = 45, message = "Los apellidos no pueden tener más de 45 caracteres")
    private String apellidos;

    @Basic(optional = false)
    private boolean active = true;

    /**
     * Crea una instancia de {@link UsuarioRegistroDto} a partir de una entidad {@link Usuario}.
     *
     * @param usuario Entidad de usuario a convertir en DTO.
     * @return Una instancia de {@link UsuarioRegistroDto} con los datos del usuario proporcionado.
     */
    public static UsuarioRegistroDto from(Usuario usuario) {
        UsuarioRegistroDto dto = new UsuarioRegistroDto();
        dto.setId(usuario.getId());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setPassword(usuario.getPassword());
        dto.setEmail(usuario.getDetalles().getEmail());
        dto.setDni(usuario.getDetalles().getDni());
        dto.setEdad(usuario.getDetalles().getEdad());
        dto.setNombre(usuario.getDetalles().getNombre());
        dto.setApellidos(usuario.getDetalles().getApellidos());
        dto.setTelefono(usuario.getDetalles().getTelefono());
        return dto;
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
     * <p>Este método asegura que los campos que llegan vacíos desde un formulario se conviertan en {@code null}
     * en lugar de quedarse como cadenas vacías, evitando posibles errores de validación.</p>
     */
    public void sanitize() {
        this.nombreUsuario = convertEmptyToNull(this.nombreUsuario);
        this.email = convertEmptyToNull(this.email);
        this.dni = convertEmptyToNull(this.dni);
        this.telefono = convertEmptyToNull(this.telefono);
        this.nombre = convertEmptyToNull(this.nombre);
        this.apellidos = convertEmptyToNull(this.apellidos);
    }
}
