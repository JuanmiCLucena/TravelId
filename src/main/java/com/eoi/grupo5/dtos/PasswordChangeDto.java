package com.eoi.grupo5.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para la funcionalidad de cambio de contraseña del usuario.
 *
 * <p>Este DTO es utilizado en el apartado de perfil del usuario, permitiendo a los usuarios
 * cambiar su contraseña actual por una nueva. Se enfoca en la gestión de los datos necesarios
 * para validar y actualizar la contraseña del usuario.</p>
 *
 * <p>Este DTO incluye campos para la contraseña actual, la nueva contraseña y la confirmación
 * de la nueva contraseña. Además, incorpora validaciones para garantizar que se cumplan
 * los requisitos de longitud y no estén vacíos.</p>
 */
@Getter
@Setter
public class PasswordChangeDto {

    /**
     * Contraseña actual del usuario.
     *
     * <p>Este campo es obligatorio y debe contener al menos 5 caracteres y un máximo de 150.
     * Representa la contraseña que el usuario está utilizando actualmente.</p>
     */
    @NotBlank(message = "Introduce la contraseña actual")
    @Size(min = 5, max = 150, message = "La contraseña debe tener entre 5 y 150 caracteres")
    private String passwordActual;

    /**
     * Nueva contraseña que el usuario desea establecer.
     *
     * <p>Este campo es obligatorio y debe cumplir con los mismos requisitos de longitud que la contraseña actual.
     * Se utiliza para almacenar la nueva contraseña que reemplazará la existente.</p>
     */
    @NotBlank(message = "Introduce la nueva contraseña")
    @Size(min = 5, max = 150, message = "La contraseña debe tener entre 5 y 150 caracteres")
    private String nuevaPassword;

    /**
     * Confirmación de la nueva contraseña.
     *
     * <p>Este campo es obligatorio y asegura que el usuario haya introducido la nueva contraseña de manera correcta,
     * al exigir que la repita. Debe coincidir con el valor de {@code nuevaPassword}.</p>
     */
    @NotBlank(message = "Vuelve a introducir la nueva contraseña")
    @Size(min = 5, max = 150, message = "La contraseña debe tener entre 5 y 150 caracteres")
    private String confirmarPassword;

}
