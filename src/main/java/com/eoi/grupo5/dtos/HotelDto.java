package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Localizacion;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.Set;

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

    public String getPrimeraImagenUrl() {
        return imagenes.stream()
                .min(Comparator.comparing(Imagen::getId))
                .map(Imagen::getUrl)
                .orElse(null);
    }


}
