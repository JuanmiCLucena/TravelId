package com.eoi.grupo5.filtros.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaCriteriaHoteles {

    private String localizacionNombre;
    private Byte categoria;
    private Integer tipoHabitacionId;
    private Integer capacidadHabitacion;
    private Integer page = 0;
    private Integer size = 10;


}
