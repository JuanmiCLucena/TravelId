package com.eoi.grupo5.controladores;

import com.eoi.grupo5.dtos.AsientoDto;
import com.eoi.grupo5.dtos.VueloDto;
import com.eoi.grupo5.filtros.criteria.BusquedaCriteriaVuelos;
import com.eoi.grupo5.mapper.VuelosMapper;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.servicios.ServicioAsiento;
import com.eoi.grupo5.servicios.ServicioMetodoPago;
import com.eoi.grupo5.servicios.ServicioVuelo;
import com.eoi.grupo5.servicios.filtros.ServicioFiltroVuelos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con los vuelos.
 * Proporciona funcionalidades para listar y mostrar detalles de los vuelos.
 */
@Controller
public class VueloController {

    private final ServicioVuelo servicioVuelo;
    private final ServicioAsiento servicioAsiento;
    private final ServicioMetodoPago servicioMetodoPago;
    private final ServicioFiltroVuelos servicioFiltroVuelos;
    private final VuelosMapper vuelosMapper;

    /**
     * Constructor que inicializa el controlador con los servicios necesarios.
     *
     * @param servicioVuelo  Servicio encargado de la lógica de negocio para los vuelos.
     * @param servicioAsiento Servicio encargado de la gestión de asientos de los vuelos.
     */
    public VueloController(ServicioVuelo servicioVuelo, ServicioAsiento servicioAsiento, ServicioMetodoPago servicioMetodoPago, ServicioFiltroVuelos servicioFiltroVuelos, VuelosMapper vuelosMapper) {
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
        this.servicioMetodoPago = servicioMetodoPago;
        this.servicioFiltroVuelos = servicioFiltroVuelos;
        this.vuelosMapper = vuelosMapper;
    }

    /**
     * Maneja la solicitud GET para mostrar la lista de vuelos disponibles.
     * Obtiene una lista paginada de vuelos y la muestra en la vista correspondiente.
     *
     * @param modelo el modelo de datos para la vista.
     * @param page   número de la página actual (por defecto 0).
     * @param size   tamaño de la página (por defecto 6).
     * @return el nombre de la vista para mostrar la lista de vuelos.
     */
    @GetMapping("vuelos/lista")
    public String listaVuelos(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        LocalDateTime fechaActual = LocalDateTime.now();

        PaginaRespuestaVuelos<Vuelo> vuelosPage = servicioVuelo.obtenerVuelosDisponiblesPaginados(page, size,fechaActual);
        List<Vuelo> vuelos = vuelosPage.getContent();
        modelo.addAttribute("lista", vuelos);
        modelo.addAttribute("page", vuelosPage);
        return "vuelos/listaVuelos";
    }

    /**
     * Maneja la solicitud GET para mostrar los detalles de un vuelo específico.
     * Busca el vuelo por su ID y carga sus detalles, incluyendo la imagen del vuelo
     * y los precios actuales de los asientos disponibles.
     *
     * @param modelo el modelo de datos para la vista.
     * @param id     el identificador del vuelo.
     * @return el nombre de la vista para mostrar los detalles del vuelo, o una vista de error si no se encuentra el vuelo.
     */
    @GetMapping("/vuelo/{id}")
    public String detallesVuelo(Model modelo, @PathVariable Integer id) {
        Optional<Vuelo> vuelo = servicioVuelo.encuentraPorId(id);
        // Si no encontramos el vuelo no hemos encontrado el hotel
        if(vuelo.isPresent()) {
            if(vuelo.get().getImagen() != null) {
                String vueloImagen = vuelo.get().getImagen().getUrl();
                modelo.addAttribute("imagenVuelo", vueloImagen);
            }
            //modelo.addAttribute("recomendados", servicioVuelo.obtenerHotelesEnTuZona(vuelo.get()));
            modelo.addAttribute("vuelo",vuelo.get());

            LocalDateTime fechaActual = LocalDateTime.now();

            // Filtrar y agrupar asientos por categoría, solo si tienen un precio válido
            Map<String, List<AsientoDto>> asientosPorCategoria = vuelo.get().getAsientos().stream()
                    .map(asiento -> {
                        double precio = 0.0;
                        // Obtener el precio del asiento
                        var precioActual = servicioAsiento.getPrecioActual(asiento, fechaActual);
                        if (precioActual != null) {
                            precio = precioActual.getValor();
                        }

                        // Verificar si el asiento está reservado y si la reserva está cancelada
                        boolean reservado = asiento.getReservas().stream()
                                .anyMatch(reserva -> !reserva.isCancelado());

                        // Crear el objeto AsientoDto
                        return new AsientoDto(
                                asiento.getId(),
                                asiento.getNumero(),
                                reservado, // Asiento reservado si tiene una reserva activa
                                asiento.getCategoria().getNombre().toLowerCase(),
                                precio
                        );
                    })
                    .filter(asientoDto -> asientoDto.getPrecio() > 0)
                    .sorted(Comparator.comparing(AsientoDto::getNumero))
                    .collect(Collectors.groupingBy(AsientoDto::getCategoria, LinkedHashMap::new, Collectors.toList()));

            modelo.addAttribute("asientosPorCategoria", asientosPorCategoria);
            modelo.addAttribute("preciosActuales",
                    servicioAsiento.obtenerPreciosActualesAsientosVuelo(vuelo.get()));
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());

            return "vuelos/detallesVuelo";
        } else {
            // Vuelo no encontrado - htlm
            return "error/paginaError";
        }

    }

    @GetMapping("/filtrar-vuelos")
    public String filtrarVuelos(Model modelo, BusquedaCriteriaVuelos criteria) {
        if (criteria.getSize() == null || criteria.getSize() <= 0) {
            criteria.setSize(6);
        }

        if (criteria.getFechaInicio() != null && criteria.getFechaFin() == null || criteria.getFechaInicio() == null && criteria.getFechaFin() != null) {
            modelo.addAttribute("error", "Debe seleccionar ambas fechas: Fecha de inicio y Fecha de fin.");
        } else {
            PaginaRespuestaVuelos<VueloDto> vuelos = servicioFiltroVuelos.buscarVuelos(
                    vuelosMapper.filtrar(criteria), criteria.getPage(), criteria.getSize());

            modelo.addAttribute("page", vuelos);
            modelo.addAttribute("lista", vuelos.getContent());
            modelo.addAttribute("origenNombre", criteria.getOrigenNombre());
            modelo.addAttribute("destinoNombre", criteria.getDestinoNombre());
            modelo.addAttribute("fechaInicio", criteria.getFechaInicio());
            modelo.addAttribute("fechaFin", criteria.getFechaFin());

        }

        return "vuelos/listaVuelos";
    }

}