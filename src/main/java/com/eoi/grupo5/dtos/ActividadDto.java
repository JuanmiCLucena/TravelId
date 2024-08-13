package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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

    public String getPrimeraImagenUrl() {
        return imagenes.stream().min(Comparator.comparing(Imagen::getId))
                .map(Imagen::getUrl)
                .orElse(null);
    }

}
