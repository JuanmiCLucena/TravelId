package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

/**
 * DTO para transferir datos de la entidad {@link Vuelo}.
 *
 * <p>Este DTO está diseñado para optimizar la transferencia de datos entre las capas de la
 * aplicación, conteniendo solo la información esencial de la entidad {@link Vuelo},
 * sin exponer las relaciones completas que están presentes en el modelo original.</p>
 *
 * <p>Dentro de la aplicación se usa para gestionar los vuelos en la parte de Usuario.
 * En lugar de manejar relaciones complejas, este DTO presenta los asientos disponibles,
 * precios e imágenes directamente como conjuntos de datos. Esto facilita la manipulación
 * en la capa de presentación y reduce la necesidad de consultas adicionales, mejorando así
 * el rendimiento y simplificando el uso del DTO en servicios que no requieren cargar toda
 * la estructura asociada a la entidad {@link Vuelo}.</p>
 */
@Builder
@Getter
@Setter
public class VueloDto {

    private Integer id;
    private String nombre;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private Localizacion origen;
    private Localizacion destino;
    private Imagen imagen;
    private Set<Asiento> asientosDisponibles;

}
