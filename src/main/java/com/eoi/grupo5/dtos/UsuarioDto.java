package com.eoi.grupo5.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para la autenticación y registro de usuarios en la aplicación.
 *
 * <p>Este DTO se emplea para gestionar las credenciales del usuario tanto en el proceso de inicio de sesión
 * como en el registro de nuevos usuarios. Asegura que los datos proporcionados sean válidos y cumplan con los
 * requisitos de seguridad necesarios.</p>
 *
 * <p>Incluye validaciones para el correo electrónico, nombre de usuario, y contraseñas, asegurando que todos los
 * campos necesarios estén presentes y sean correctos antes de proceder con la autenticación o el registro.</p>
 */
@Getter
@Setter
public class UsuarioDto {

    /**
     * Correo electrónico del usuario, utilizado para la identificación y comunicación.
     * Asegura que el correo proporcionado sea válido y no esté vacío.
     */
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    private String email;

    /**
     * Nombre de usuario, usado como identificador único dentro de la aplicación.
     * Debe tener una longitud mínima y máxima especificada.
     */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 2, max = 45, message = "El nombre de usuario debe tener entre 2 y 45 caracteres")
    private String username;

    /**
     * Contraseña del usuario para la autenticación.
     * La longitud de la contraseña está restringida para cumplir con los requisitos de seguridad.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 5, max = 150, message = "La contraseña debe tener entre 5 y 150 caracteres")
    private String password;

    /**
     * Confirmación de la contraseña, para verificar que el usuario haya introducido correctamente la contraseña.
     * Debe coincidir con el campo {@code password}.
     */
    @NotBlank(message = "Debe repetir la contraseña")
    private String repeatPassword;
}
