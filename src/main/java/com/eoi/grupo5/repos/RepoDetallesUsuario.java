package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.DetallesUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link DetallesUsuario}.
 *
 * <p>Proporciona métodos CRUD para gestionar los detalles de usuario en la base de datos,
 * así como una funcionalidad adicional para verificar la existencia de un email.</p>
 */
@Repository
public interface RepoDetallesUsuario extends JpaRepository<DetallesUsuario, Integer> {

    /**
     * Verifica si un correo electrónico ya existe en la base de datos.
     *
     * @param email El correo electrónico a verificar.
     * @return {@code true} si el correo electrónico ya está registrado, {@code false} en caso contrario.
     */
    boolean existsByEmail(String email);
}
