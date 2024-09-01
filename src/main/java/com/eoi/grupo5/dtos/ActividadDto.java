package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

/**
 * DTO para transferir datos de la entidad {@link Actividad}.
 *
 * <p>Este DTO está diseñado para optimizar la transferencia de datos entre las capas de la
 * aplicación, conteniendo solo la información esencial de la entidad {@link Actividad},
 * sin exponer las relaciones completas que están presentes en el modelo original.</p>
 *
 * <p>Dentro de la aplicación se usa para gestionar las actividades en la parte de Usuario.
 * En lugar de manejar relaciones complejas, este DTO presenta los precios e imágenes
 * directamente como conjuntos de datos. Esto facilita la manipulación en la capa de
 * presentación y reduce la necesidad de consultas adicionales, mejorando así el rendimiento
 * y simplificando el uso del DTO en servicios que no requieren cargar toda la estructura
 * asociada a la entidad {@link Actividad}.</p>
 */
@Builder
@Getter
@Setter
public class ActividadDto {

    private Integer id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private TipoActividad tipo;
    private Localizacion localizacion;
    private Set<Precio> precio;
    private Set<Imagen> imagenes;

    /**
     * Obtiene la URL de la primera imagen asociada a la actividad.
     *
     * <p>Este método no está en uso actualmente, pero se incluye para
     * posibles futuras funcionalidades donde se necesite obtener una
     * representación visual de la actividad de manera sencilla.</p>
     *
     * @return URL de la primera imagen, o `null` si no hay imágenes disponibles.
     */
    public String getPrimeraImagenUrl() {
        return imagenes.stream()
                .min(Comparator.comparing(Imagen::getId))
                .map(Imagen::getUrl)
                .orElse(null);
    }

}
