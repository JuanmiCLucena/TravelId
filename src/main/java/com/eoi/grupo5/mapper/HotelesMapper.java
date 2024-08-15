package com.eoi.grupo5.mapper;


import com.eoi.grupo5.dtos.ActividadDto;
import com.eoi.grupo5.dtos.HotelDto;
import com.eoi.grupo5.filtros.FiltroActividades;
import com.eoi.grupo5.filtros.FiltroHoteles;
import com.eoi.grupo5.filtros.criteria.BusquedaCriteriaActividades;
import com.eoi.grupo5.filtros.criteria.BusquedaCriteriaHoteles;
import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
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
public interface HotelesMapper {

    FiltroHoteles filtrar(BusquedaCriteriaHoteles busquedaCriteriaHoteles);

    @Mapping(source = "habitaciones", target = "habitaciones")
    @Mapping(source= "imagenesHotel", target = "imagenes")
    HotelDto hotelDto(Hotel hotel);

    List<HotelDto> hotelesDto(List<Hotel> hoteles);

    default PaginaRespuestaHoteles<HotelDto> paginaRespuestaHoteles(Page<Hotel> pagina) {
        PaginaRespuestaHoteles<HotelDto> paginaRespuesta = new PaginaRespuestaHoteles<>();
        paginaRespuesta.setContent(hotelesDto(pagina.getContent()));
        paginaRespuesta.setPage(pagina.getNumber());
        paginaRespuesta.setSize(pagina.getSize());
        paginaRespuesta.setTotalPages(pagina.getTotalPages());
        paginaRespuesta.setTotalSize(pagina.getTotalElements());
        return paginaRespuesta;
    }


}

