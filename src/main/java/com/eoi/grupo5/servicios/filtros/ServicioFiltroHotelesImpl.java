package com.eoi.grupo5.servicios.filtros;

import com.eoi.grupo5.dtos.HotelDto;
import com.eoi.grupo5.filtros.FiltroActividades;
import com.eoi.grupo5.filtros.FiltroHoteles;
import com.eoi.grupo5.filtros.specification.HotelSpec;
import com.eoi.grupo5.mapper.HotelesMapper;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
import com.eoi.grupo5.repos.RepoHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ServicioFiltroHotelesImpl implements ServicioFiltroHoteles {

    private final RepoHotel repoHotel;
    private final HotelesMapper hotelesMapper;

    public ServicioFiltroHotelesImpl(RepoHotel repoHotel, HotelesMapper hotelesMapper) {
        this.repoHotel = repoHotel;
        this.hotelesMapper = hotelesMapper;
    }

    @Override
    public PaginaRespuestaHoteles<HotelDto> buscarHoteles(FiltroHoteles filtro, int page, int size) {
        Specification<Hotel> spec = HotelSpec.filtrarPor(filtro);
        Page<Hotel> resultadoPagina = repoHotel.findAll(spec, PageRequest.of(page, size));
        return hotelesMapper.paginaRespuestaHoteles(resultadoPagina);
    }
}
