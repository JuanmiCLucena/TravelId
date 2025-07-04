package com.eoi.grupo5.filtros.criteria;

import com.eoi.grupo5.modelos.TipoActividad;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaCriteriaActividades {

    private String localizacionNombre;
    private Integer tipoId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer page = 0;
    private Integer size = 10;


}
