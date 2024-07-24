package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;

import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.dtos.ActividadDto;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.repos.RepoActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ServicioActividad extends AbstractBusinessServiceSoloEnt<Actividad, Integer, RepoActividad>{

    protected ServicioActividad(RepoActividad repoActividad) {
        super(repoActividad);
    }

    public PaginaRespuestaActividades<Actividad> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Actividad> actividadPage = getRepo().findAll((Specification<Actividad>) null, pageable);

        PaginaRespuestaActividades<Actividad> respuesta = new PaginaRespuestaActividades<>();
        respuesta.setContent(actividadPage.getContent());
        respuesta.setSize(actividadPage.getSize());
        respuesta.setTotalSize(actividadPage.getTotalElements());
        respuesta.setPage(actividadPage.getNumber());
        respuesta.setTotalPages(actividadPage.getTotalPages());

        return respuesta;
    }

    public List<Actividad> obtenerActividadesEnTuZona(Actividad actividad) {
        return super.buscarEntidades()
                .stream()
                .filter(a -> a.getLocalizacion().getNombre().equals(actividad.getLocalizacion().getNombre()) && !Objects.equals(a.getId(), actividad.getId()))
                .limit(2)
                .toList();
    }


    public Precio getPrecioActual(Actividad actividad, LocalDateTime fechaActual) {
        return actividad.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) && (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

    public Map<Integer, Double> obtenerPreciosActualesActividades(List<Actividad> actividades) {
        // Obtener los precios actuales de las actividades del hotel
        LocalDateTime fechaActual = LocalDateTime.now();
        Map<Integer, Double> preciosActuales = new HashMap<>();

        actividades.forEach(actividad -> {
            Precio precioActual = getPrecioActual(actividad, fechaActual);
            if (precioActual != null) {
                preciosActuales.put(actividad.getId(), precioActual.getValor());
            } else {
                preciosActuales.put(actividad.getId(), null);
            }
        });

        return preciosActuales;
    }

    public Precio getPrecioActual(ActividadDto actividad, LocalDateTime fechaActual) {
        return actividad.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) && (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

    public Map<Integer, Double> obtenerPreciosActualesActividades(PaginaRespuestaActividades<ActividadDto> actividades) {
        // Obtener los precios actuales de las actividades del hotel
        LocalDateTime fechaActual = LocalDateTime.now();
        Map<Integer, Double> preciosActuales = new HashMap<>();

        actividades.forEach(actividad -> {
            Precio precioActual = getPrecioActual(actividad, fechaActual);
            if (precioActual != null) {
                preciosActuales.put(actividad.getId(), precioActual.getValor());
            } else {
                preciosActuales.put(actividad.getId(), null);
            }
        });

        return preciosActuales;
    }



}
