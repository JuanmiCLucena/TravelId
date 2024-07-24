package com.eoi.grupo5.servicios.filtros;

import com.eoi.grupo5.mapper.ActividadesMapper;
import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.dtos.ActividadDto;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.repos.RepoActividad;
import com.eoi.grupo5.repos.filtros.FiltroActividades;
import com.eoi.grupo5.repos.specification.ActividadSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ServicioFiltroActividadesImpl implements ServicioFiltroActividades {

    private final RepoActividad repoActividad;
    private final ActividadesMapper actividadMapper;

    public ServicioFiltroActividadesImpl(RepoActividad repoActividad, ActividadesMapper actividadMapper) {
        this.repoActividad = repoActividad;
        this.actividadMapper = actividadMapper;
    }

    @Override
    public PaginaRespuestaActividades<ActividadDto> buscarActividades(FiltroActividades filtro, int page, int size) {
        Specification<Actividad> spec = ActividadSpec.filtrarPor(filtro);
        Page<Actividad> resultadoPagina = repoActividad.findAll(spec, PageRequest.of(page, size));
        return actividadMapper.paginaRespuestaActividades(resultadoPagina);
    }
}
