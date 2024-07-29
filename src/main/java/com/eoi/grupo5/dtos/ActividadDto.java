package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.modelos.TipoActividad;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
        return imagenes.stream()
                .findFirst()
                .map(Imagen::getUrl)
                .orElse(null);
    }

}
