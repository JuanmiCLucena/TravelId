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

/**
 * Controlador encargado de gestionar las operaciones relacionadas con las actividades.
 * Proporciona funcionalidades para listar, filtrar y mostrar detalles de las actividades.
 */
@Controller
public class ActividadController {

    private final ServicioActividad servicioActividad;
    private final ServicioTipoActividad servicioTipoActividad;
    private final ServicioMetodoPago servicioMetodoPago;
    private final ServicioFiltroActividades servicioFiltroActividades;
    private final ActividadesMapper actividadesMapper;

    /**
     * Constructor que inicializa el controlador con los servicios necesarios.
     *
     * @param servicioActividad       Servicio encargado de la lógica de negocio para las actividades.
     * @param servicioTipoActividad   Servicio encargado de gestionar los tipos de actividades.
     * @param servicioMetodoPago       Servicio encargado de gestionar los métodos de pago.
     * @param servicioFiltroActividades Servicio encargado de aplicar filtros de búsqueda en actividades.
     * @param actividadesMapper        Mapper para transformar entidades de actividad.
     */
    public ActividadController(ServicioActividad servicioActividad, ServicioTipoActividad servicioTipoActividad,
                               ServicioMetodoPago servicioMetodoPago, ServicioFiltroActividades servicioFiltroActividades,
                               ActividadesMapper actividadesMapper) {
        this.servicioActividad = servicioActividad;
        this.servicioTipoActividad = servicioTipoActividad;
        this.servicioMetodoPago = servicioMetodoPago;
        this.servicioFiltroActividades = servicioFiltroActividades;
        this.actividadesMapper = actividadesMapper;
    }

    /**
     * Maneja la solicitud GET para mostrar la lista de actividades disponibles.
     *
     * Obtiene las actividades disponibles de acuerdo con la fecha actual y las muestra en la página de lista de actividades.
     * También se cargan los tipos de actividades y los precios actuales de cada una.
     *
     * @param modelo el modelo de datos para la vista.
     * @return el nombre de la vista para mostrar la lista de actividades.
     */
    @GetMapping("/actividades/lista")
    public String listaActividades(Model modelo) {
        int page = 0;
        int size = 6;
        LocalDateTime fechaActual = LocalDateTime.now();

        PaginaRespuestaActividades<Actividad> actividadesPage = servicioActividad.obtenerActividadesDisponiblesPaginadas(page, size, fechaActual);
        List<Actividad> actividades = actividadesPage.getContent();

        modelo.addAttribute("lista", actividades);
        modelo.addAttribute("preciosActuales", servicioActividad.obtenerPreciosActualesActividades(actividades));
        modelo.addAttribute("tiposActividad", servicioTipoActividad.buscarEntidades());
        modelo.addAttribute("page", actividadesPage);

        return "actividades/listaActividades";
    }

    /**
     * Maneja la solicitud GET para mostrar los detalles de una actividad específica.
     *
     * Busca la actividad por su ID y carga sus detalles, incluyendo imagen, métodos de pago disponibles,
     * actividades recomendadas y el número de plazas disponibles.
     *
     * @param modelo el modelo de datos para la vista.
     * @param id     el identificador de la actividad.
     * @return el nombre de la vista para mostrar los detalles de la actividad, o una página de error si no se encuentra la actividad.
     */
    @GetMapping("/actividad/{id}")
    public String detallesActividad(Model modelo, @PathVariable Integer id) {
        Optional<Actividad> optionalActividad = servicioActividad.encuentraPorId(id);

        if (optionalActividad.isPresent()) {
            Actividad actividad = optionalActividad.get();
            Optional<Imagen> optionalActividadImagen = actividad.getImagenes().stream().findFirst();

            if (optionalActividadImagen.isPresent()) {
                String actividadImagen = optionalActividadImagen.get().getUrl();
                modelo.addAttribute("imagenActividad", actividadImagen);
            }

            modelo.addAttribute("recomendados", servicioActividad.obtenerActividadesEnTuZona(actividad));
            modelo.addAttribute("actividad", actividad);
            modelo.addAttribute("precioActual", servicioActividad.getPrecioActual(actividad, LocalDateTime.now()));
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());

            Integer plazasDisponibles = actividad.getMaximosAsistentes() - actividad.getAsistentesConfirmados();
            modelo.addAttribute("plazasDisponibles", plazasDisponibles);

            return "actividades/detallesActividad";
        } else {
            // Si no se encuentra la actividad, se muestra una página de error
            return "error/paginaError";
        }
    }

    /**
     * Maneja la solicitud GET para filtrar las actividades basadas en los criterios de búsqueda proporcionados.
     *
     * Aplica los filtros según los criterios (como fechas, tipo de actividad, y localización) y muestra los resultados
     * en la página de lista de actividades. Si los filtros no son válidos, muestra un mensaje de error.
     *
     * @param modelo  el modelo de datos para la vista.
     * @param criteria los criterios de búsqueda para filtrar las actividades.
     * @return el nombre de la vista para mostrar la lista de actividades filtradas, o la vista con un mensaje de error si los filtros no son válidos.
     */
    @GetMapping("/filtrar-actividades")
    public String filtrarActividades(Model modelo, BusquedaCriteriaActividades criteria) {
        if (criteria.getSize() == null || criteria.getSize() <= 0) {
            criteria.setSize(6);
        }

        if (criteria.getFechaInicio() != null && criteria.getFechaFin() == null || criteria.getFechaInicio() == null && criteria.getFechaFin() != null) {
            modelo.addAttribute("error", "Debe seleccionar ambas fechas: Fecha de inicio y Fecha de fin.");
        } else {
            if (criteria.getTipoId() == null) {
                PaginaRespuestaActividades<ActividadDto> actividades = servicioFiltroActividades.buscarActividades(
                        actividadesMapper.filtrar(criteria), criteria.getPage(), criteria.getSize());

                modelo.addAttribute("page", actividades);
                modelo.addAttribute("lista", actividades.getContent());
                modelo.addAttribute("fechaInicio", criteria.getFechaInicio());
                modelo.addAttribute("fechaFin", criteria.getFechaFin());
                modelo.addAttribute("localizacionNombre", criteria.getLocalizacionNombre());
                modelo.addAttribute("preciosActuales", servicioActividad.obtenerPreciosActualesActividades(actividades));
                modelo.addAttribute("tiposActividad", servicioTipoActividad.buscarEntidades());

                return "actividades/listaActividades";
            } else {
                modelo.addAttribute("tipoId", criteria.getTipoId());
                modelo.addAttribute("fechaInicio", criteria.getFechaInicio());
                modelo.addAttribute("fechaFin", criteria.getFechaFin());
                modelo.addAttribute("localizacionNombre", criteria.getLocalizacionNombre());

                PaginaRespuestaActividades<ActividadDto> actividades = servicioFiltroActividades.buscarActividades(
                        actividadesMapper.filtrar(criteria), criteria.getPage(), criteria.getSize());

                modelo.addAttribute("page", actividades);
                modelo.addAttribute("lista", actividades.getContent());

                Map<Integer, Double> preciosActuales = servicioActividad.obtenerPreciosActualesActividades(actividades);
                modelo.addAttribute("preciosActuales", preciosActuales);
            }
        }

        modelo.addAttribute("tiposActividad", servicioTipoActividad.buscarEntidades());
        return "actividades/listaActividades";
    }
}
