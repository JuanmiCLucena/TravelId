package com.eoi.grupo5.modelos.filtros;

import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.modelos.TipoActividad;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ActividadDto {

    private Integer id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private TipoActividad tipo;
    private Localizacion localizacion;
    private List<Precio> precio;

    public Precio obtenerPrecioActual(ActividadDto actividad, LocalDateTime fechaActual) {
        return actividad.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) && (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

}
