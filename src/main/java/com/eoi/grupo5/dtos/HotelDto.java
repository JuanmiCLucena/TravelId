package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Localizacion;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.Set;

/**
 * DTO para la gestión de hoteles en la parte de usuario.
 *
 * <p>Este DTO es utilizado para mostrar la información relevante de los hoteles
 * en la interfaz de usuario, incluyendo detalles como nombre, descripción,
 * categoría, contacto, localización, habitaciones, e imágenes.</p>
 *
 * <p>Al ser diseñado para la parte de usuario, este DTO se enfoca en la visualización
 * de los hoteles, permitiendo su uso en la paginación y otras funcionalidades
 * relacionadas con la presentación de datos en la aplicación.</p>
 *
 * <p>A diferencia de la entidad Hotel, en este DTO las habitaciones y las imágenes
 * son tratadas como colecciones para facilitar el acceso y manejo en la interfaz.</p>
 */
@Builder
@Getter
@Setter
public class HotelDto {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Byte categoria;
    private String contacto;
    private Set<Habitacion> habitaciones;
    private Localizacion localizacion;
    private Set<Imagen> imagenes;

    /**
     * Obtiene la URL de la primera imagen del hotel.
     *
     * <p>Este método retorna la URL de la imagen con el ID más bajo,
     * lo que se utiliza comúnmente para mostrar la imagen principal
     * en las listas de hoteles.</p>
     *
     * <p>Este método está presente para posibles funcionalidades futuras
     * o actualizaciones del sistema.</p>
     *
     * @return La URL de la primera imagen, o null si no hay imágenes disponibles.
     */
    public String getPrimeraImagenUrl() {
        return imagenes.stream()
                .min(Comparator.comparing(Imagen::getId))
                .map(Imagen::getUrl)
                .orElse(null);
    }
}
