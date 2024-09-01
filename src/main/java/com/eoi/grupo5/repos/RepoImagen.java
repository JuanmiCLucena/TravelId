package com.eoi.grupo5.repos;

import com.eoi.grupo5.modelos.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Imagen}.
 *
 * <p>Proporciona operaciones CRUD para gestionar las imágenes almacenadas en la base de datos.</p>
 */
@Repository
public interface RepoImagen extends JpaRepository<Imagen, Integer> {
}
