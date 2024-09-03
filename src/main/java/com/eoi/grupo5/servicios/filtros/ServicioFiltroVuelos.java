package com.eoi.grupo5.servicios.filtros;

import com.eoi.grupo5.dtos.VueloDto;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.filtros.FiltroVuelos;

public interface ServicioFiltroVuelos {
    PaginaRespuestaVuelos<VueloDto> buscarVuelos(FiltroVuelos filtro, int page, int size);
}
