package com.eoi.grupo5.servicios.filtros;

import com.eoi.grupo5.dtos.ActividadDto;
import com.eoi.grupo5.dtos.HotelDto;
import com.eoi.grupo5.filtros.FiltroActividades;
import com.eoi.grupo5.filtros.FiltroHoteles;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;

public interface ServicioFiltroHoteles {

    PaginaRespuestaHoteles<HotelDto> buscarHoteles(FiltroHoteles filtro, int page, int size);
}
