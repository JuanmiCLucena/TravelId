package com.eoi.grupo5.servicios.filtros;

import com.eoi.grupo5.dtos.VueloDto;
import com.eoi.grupo5.mapper.VuelosMapper;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.repos.RepoVuelo;
import com.eoi.grupo5.filtros.FiltroVuelos;
import com.eoi.grupo5.filtros.specification.VueloSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ServicioFiltroVuelosImpl implements ServicioFiltroVuelos {

    private final RepoVuelo repoVuelo;
    private final VuelosMapper vuelosMapper;

    public ServicioFiltroVuelosImpl(RepoVuelo repoVuelo, VuelosMapper vuelosMapper) {
        this.repoVuelo = repoVuelo;
        this.vuelosMapper = vuelosMapper;
    }

    @Override
    public PaginaRespuestaVuelos<VueloDto> buscarVuelos(FiltroVuelos filtro, int page, int size) {
        Specification<Vuelo> spec = VueloSpec.filtrarPor(filtro);
        Page<Vuelo> resultadoPagina = repoVuelo.findAll(spec, PageRequest.of(page, size));
        return vuelosMapper.paginaRespuestaVuelos(resultadoPagina);
    }
}
