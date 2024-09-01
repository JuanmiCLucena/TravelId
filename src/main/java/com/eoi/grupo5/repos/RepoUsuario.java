package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Usuario;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link Usuario}.
 *
 * <p>Este repositorio proporciona operaciones CRUD básicas para la gestión de usuarios en la base de datos.</p>
 *
 * <p>Hereda las funcionalidades de {@link JpaRepository}, lo que incluye operaciones para crear, leer, actualizar y eliminar registros de {@link Usuario}.</p>
 */
@Repository
public interface RepoUsuario extends JpaRepository<Usuario, Integer> {

    /**
     * Encuentra un usuario por su nombre de usuario.
     *
     * @param nombreUsuario El nombre de usuario para buscar.
     * @return Un {@link Optional} que contiene el {@link Usuario} si se encuentra, o {@link Optional#empty()} si no.
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /**
     * Encuentra todos los usuarios con paginación.
     *
     * @param pageable Objeto que define la página actual y el tamaño de página.
     * @return Una página de {@link Usuario} con los resultados solicitados.
     */
    Page<Usuario> findAll(@Nonnull Pageable pageable);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado.
     *
     * @param username El nombre de usuario a verificar.
     * @return {@code true} si existe un usuario con el nombre dado, {@code false} en caso contrario.
     */
    boolean existsBynombreUsuario(String username);
}
