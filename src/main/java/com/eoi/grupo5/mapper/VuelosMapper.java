package com.eoi.grupo5.mapper;

import com.eoi.grupo5.dtos.VueloDto;
import com.eoi.grupo5.filtros.FiltroVuelos;
import com.eoi.grupo5.filtros.criteria.BusquedaCriteriaVuelos;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
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
public interface VuelosMapper {

    FiltroVuelos filtrar(BusquedaCriteriaVuelos busquedaCriteriaVuelos);



    @Mapping(source = "imagen", target = "imagen")
    VueloDto vueloDto(Vuelo vuelo);

    List<VueloDto> vuelosDto(List<Vuelo> vuelos);

    default PaginaRespuestaVuelos<VueloDto> paginaRespuestaVuelos(Page<Vuelo> pagina) {
        PaginaRespuestaVuelos<VueloDto> paginaRespuesta = new PaginaRespuestaVuelos<>();
        paginaRespuesta.setContent(vuelosDto(pagina.getContent()));
        paginaRespuesta.setPage(pagina.getNumber());
        paginaRespuesta.setSize(pagina.getSize());
        paginaRespuesta.setTotalPages(pagina.getTotalPages());
        paginaRespuesta.setTotalSize(pagina.getTotalElements());
        return paginaRespuesta;
    }
}
