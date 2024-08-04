package com.eoi.grupo5.controladores;

import com.eoi.grupo5.filtros.criteria.BusquedaCriteriaActividades;
import com.eoi.grupo5.mapper.ActividadesMapper;
import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Imagen;

import com.eoi.grupo5.dtos.ActividadDto;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.servicios.ServicioActividad;
import com.eoi.grupo5.servicios.ServicioMetodoPago;
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
    private final ServicioMetodoPago servicioMetodoPago;

    // Filtro
    private final ServicioFiltroActividades servicioFiltroActividades;
    private final ActividadesMapper actividadesMapper;

    public ActividadController(ServicioActividad servicioActividad, ServicioTipoActividad servicioTipoActividad, ServicioMetodoPago servicioMetodoPago, ServicioFiltroActividades servicioFiltroActividades, ActividadesMapper actividadesMapper) {
        this.servicioActividad = servicioActividad;
        this.servicioTipoActividad = servicioTipoActividad;
        this.servicioMetodoPago = servicioMetodoPago;
        this.servicioFiltroActividades = servicioFiltroActividades;
        this.actividadesMapper = actividadesMapper;
    }

    @GetMapping("/actividades/lista")
    public String listaActividades(Model modelo) {

        int page = 0;
        int size = 6;

        LocalDateTime fechaActual = LocalDateTime.now();

        PaginaRespuestaActividades<Actividad> actividadesPage = servicioActividad.obtenerActividadesDisponiblesPaginadas(page, size,fechaActual);
        List<Actividad> actividades = actividadesPage.getContent();
        modelo.addAttribute("lista", actividades);
        modelo.addAttribute("preciosActuales", servicioActividad.obtenerPreciosActualesActividades(actividades));
        modelo.addAttribute("tiposActividad", servicioTipoActividad.buscarEntidades());
        modelo.addAttribute("page", actividadesPage);
        return "actividades/listaActividades";
    }

    @GetMapping("/actividad/{id}")
    public String detallesActividad(Model modelo, @PathVariable Integer id) {
        Optional<Actividad> optionalActividad = servicioActividad.encuentraPorId(id);
        // Si no encontramos el hotel no hemos encontrado el hotel
        if(optionalActividad.isPresent()) {
            Actividad actividad = optionalActividad.get();
            Optional<Imagen> optionalActividadImagen = actividad.getImagenes().stream().findFirst();
            if(optionalActividadImagen.isPresent()) {
                String actividadImagen = optionalActividadImagen.get().getUrl();
                modelo.addAttribute("imagenActividad", actividadImagen);
            }
            modelo.addAttribute("recomendados", servicioActividad.obtenerActividadesEnTuZona(actividad));
            modelo.addAttribute("actividad",actividad);
            modelo.addAttribute("precioActual",
                    servicioActividad.getPrecioActual(actividad, LocalDateTime.now()));
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());

            Integer plazasDisponibles = actividad.getMaximosAsistentes() - actividad.getAsistentesConfirmados();

            modelo.addAttribute("plazasDisponibles", plazasDisponibles);

            return "actividades/detallesActividad";
        } else {
            // Hotel no encontrado - htlm
            return "error/paginaError";
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
                return "actividades/listaActividades";
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
        return "actividades/listaActividades";
    }

}
