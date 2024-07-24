package com.eoi.grupo5.mapper;


import com.eoi.grupo5.controladores.criteria.BusquedaCriteriaActividades;
import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.dtos.ActividadDto;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.repos.filtros.FiltroActividades;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;


import java.util.List;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(
        componentModel = SPRING,
        injectionStrategy = CONSTRUCTOR
)
public interface ActividadesMapper {

    FiltroActividades filtrar(BusquedaCriteriaActividades busquedaCriteriaActividades);

    @Mapping(source = "tipo", target = "tipo")
    @Mapping(source = "precio", target = "precio")
    ActividadDto actividadDto(Actividad actividad);

    List<ActividadDto> actividadesDto(List<Actividad> actividades);

    default PaginaRespuestaActividades<ActividadDto> paginaRespuestaActividades(Page<Actividad> pagina) {
        PaginaRespuestaActividades<ActividadDto> paginaRespuesta = new PaginaRespuestaActividades<>();
        paginaRespuesta.setContent(actividadesDto(pagina.getContent()));
        paginaRespuesta.setPage(pagina.getNumber());
        paginaRespuesta.setSize(pagina.getSize());
        paginaRespuesta.setTotalPages(pagina.getTotalPages());
        paginaRespuesta.setTotalSize(pagina.getTotalElements());
        return paginaRespuesta;
    }


}

