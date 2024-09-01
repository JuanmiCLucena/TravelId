package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.servicios.ServicioAsiento;
import com.eoi.grupo5.servicios.ServicioVuelo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con los vuelos.
 * Proporciona funcionalidades para listar y mostrar detalles de los vuelos.
 */
@Controller
public class VueloController {

    private final ServicioVuelo servicioVuelo;
    private final ServicioAsiento servicioAsiento;

    /**
     * Constructor que inicializa el controlador con los servicios necesarios.
     *
     * @param servicioVuelo  Servicio encargado de la lógica de negocio para los vuelos.
     * @param servicioAsiento Servicio encargado de la gestión de asientos de los vuelos.
     */
    public VueloController(ServicioVuelo servicioVuelo, ServicioAsiento servicioAsiento) {
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
    }

    /**
     * Maneja la solicitud GET para mostrar la lista de vuelos disponibles.
     *
     * Obtiene una lista paginada de vuelos y la muestra en la vista correspondiente.
     *
     * @param modelo el modelo de datos para la vista.
     * @param page   número de la página actual (por defecto 0).
     * @param size   tamaño de la página (por defecto 6).
     * @return el nombre de la vista para mostrar la lista de vuelos.
     */
    @GetMapping("vuelos/lista")
    public String listaVuelos(Model modelo,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "6") int size) {

        LocalDateTime fechaActual = LocalDateTime.now();
        PaginaRespuestaVuelos<Vuelo> vuelosPage = servicioVuelo.obtenerVuelosDisponiblesPaginados(page, size, fechaActual);
        List<Vuelo> vuelos = vuelosPage.getContent();
        modelo.addAttribute("lista", vuelos);
        modelo.addAttribute("page", vuelosPage);

        return "vuelos/listaVuelos";
    }

    /**
     * Maneja la solicitud GET para mostrar los detalles de un vuelo específico.
     *
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

        if (vuelo.isPresent()) {
            if (vuelo.get().getImagen() != null) {
                String vueloImagen = vuelo.get().getImagen().getUrl();
                modelo.addAttribute("imagenVuelo", vueloImagen);
            }
            modelo.addAttribute("vuelo", vuelo.get());
            modelo.addAttribute("preciosActuales", servicioAsiento.obtenerPreciosActualesAsientosVuelo(vuelo.get()));

            return "vuelos/detallesVuelo";
        } else {
            // Si no se encuentra el vuelo, se muestra una página de error
            return "vueloNoEncontrado";
        }
    }
}
