package com.eoi.grupo5.controladores;

import com.eoi.grupo5.controladores.criteria.BusquedaCriteriaActividades;
import com.eoi.grupo5.mapper.ActividadesMapper;
import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Imagen;

import com.eoi.grupo5.dtos.ActividadDto;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.servicios.ServicioActividad;
import com.eoi.grupo5.servicios.ServicioTipoActividad;
import com.eoi.grupo5.servicios.filtros.ServicioFiltroActividades;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ActividadController {

    private final ServicioActividad servicioActividad;
    private final ServicioTipoActividad servicioTipoActividad;

    // Filtro
    private final ServicioFiltroActividades servicioFiltroActividades;
    private final ActividadesMapper actividadesMapper;

    public ActividadController(ServicioActividad servicioActividad, ServicioTipoActividad servicioTipoActividad, ServicioFiltroActividades servicioFiltroActividades, ActividadesMapper actividadesMapper) {
        this.servicioActividad = servicioActividad;
        this.servicioTipoActividad = servicioTipoActividad;
        this.servicioFiltroActividades = servicioFiltroActividades;
        this.actividadesMapper = actividadesMapper;
    }

    @GetMapping("/actividades/lista")
    public String listaActividades(Model modelo) {

        int page = 0;
        int size = 6;

        PaginaRespuestaActividades<Actividad> actividadesPage = servicioActividad.buscarEntidadesPaginadas(page, size);
        List<Actividad> actividades = actividadesPage.getContent();
        modelo.addAttribute("lista", actividades);
        modelo.addAttribute("preciosActuales", servicioActividad.obtenerPreciosActualesActividades(actividades));
        modelo.addAttribute("tiposActividad", servicioTipoActividad.buscarEntidades());
        modelo.addAttribute("page", actividadesPage);
        return "actividades";
    }

    @GetMapping("/actividad/{id}")
    public String detallesActividad(Model modelo, @PathVariable Integer id) {
        Optional<Actividad> actividad = servicioActividad.encuentraPorId(id);
        // Si no encontramos el hotel no hemos encontrado el hotel
        if(actividad.isPresent()) {
            Optional<Imagen> optionalActividadImagen = actividad.get().getImagenes().stream().findFirst();
            if(optionalActividadImagen.isPresent()) {
                String actividadImagen = optionalActividadImagen.get().getUrl();
                modelo.addAttribute("imagenActividad", actividadImagen);
            }
            modelo.addAttribute("recomendados", servicioActividad.obtenerActividadesEnTuZona(actividad.get()));
            modelo.addAttribute("actividad",actividad.get());
            modelo.addAttribute("precioActual",
                    servicioActividad.getPrecioActual(actividad.get(), LocalDateTime.now()));

            return "detallesActividad";
        } else {
            // Hotel no encontrado - htlm
            return "hotelNoEncontrado";
        }

    }

    @GetMapping("/filtrar-actividades")
    public String filtrarActividades(Model modelo, BusquedaCriteriaActividades criteria) {

        if(criteria.getSize() == null || criteria.getSize() <= 0) {
            criteria.setSize(6);
        }

        if(criteria.getFechaInicio() != null && criteria.getFechaFin() == null || criteria.getFechaInicio() == null && criteria.getFechaFin() !=null) {
            modelo.addAttribute("error", "Debe seleccionar ambas fechas: Fecha de inicio y Fecha de fin.");
        } else {
            if(criteria.getTipoId() == null) {
                PaginaRespuestaActividades<ActividadDto> actividades;
                actividades = servicioFiltroActividades.buscarActividades(actividadesMapper.filtrar(criteria), criteria.getPage(), criteria.getSize());
                modelo.addAttribute("page", actividades);
                modelo.addAttribute("lista", actividades.getContent());
                modelo.addAttribute("fechaInicio", criteria.getFechaInicio());
                modelo.addAttribute("fechaFin", criteria.getFechaFin());
                modelo.addAttribute("preciosActuales", servicioActividad
                        .obtenerPreciosActualesActividades(actividades));
                modelo.addAttribute("tiposActividad", servicioTipoActividad.buscarEntidades());
                return "actividades";
            } else {

                modelo.addAttribute("tipoId", criteria.getTipoId());
                modelo.addAttribute("fechaInicio", criteria.getFechaInicio());
                modelo.addAttribute("fechaFin", criteria.getFechaFin());
                PaginaRespuestaActividades<ActividadDto> actividades = servicioFiltroActividades
                        .buscarActividades(actividadesMapper.filtrar(criteria), criteria.getPage(), criteria.getSize());
                modelo.addAttribute("page",actividades);
                modelo.addAttribute("lista", actividades.getContent());
                // Obtener los precios actuales de las habitaciones del actividad
               Map<Integer, Double> preciosActuales = servicioActividad.obtenerPreciosActualesActividades(actividades);

                modelo.addAttribute("preciosActuales", preciosActuales);
            }
        }

        modelo.addAttribute("tiposActividad", servicioTipoActividad.buscarEntidades());
        return "actividades";
    }

}
