package com.eoi.grupo5.controladores;

import com.eoi.grupo5.dtos.HotelDto;
import com.eoi.grupo5.filtros.criteria.BusquedaCriteriaHoteles;
import com.eoi.grupo5.mapper.HotelesMapper;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.ServicioHotel;
import com.eoi.grupo5.servicios.ServicioTipoHabitacion;
import com.eoi.grupo5.servicios.filtros.ServicioFiltroHoteles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con los hoteles.
 * Proporciona funcionalidades para listar, filtrar y mostrar detalles de los hoteles.
 */
@Controller
public class HotelController {

    private final ServicioHotel servicioHotel;
    private final ServicioHabitacion servicioHabitacion;
    private final ServicioTipoHabitacion servicioTipoHabitacion;
    private final ServicioFiltroHoteles servicioFiltroHoteles;
    private final HotelesMapper hotelesMapper;

    /**
     * Constructor que inicializa el controlador con los servicios necesarios.
     *
     * @param servicioHotel         Servicio encargado de la lógica de negocio para los hoteles.
     * @param servicioHabitacion     Servicio encargado de la gestión de habitaciones de los hoteles.
     * @param servicioTipoHabitacion Servicio encargado de gestionar los tipos de habitaciones.
     * @param servicioFiltroHoteles  Servicio encargado de aplicar filtros de búsqueda en hoteles.
     * @param hotelesMapper          Mapper para transformar entidades de hotel.
     */
    public HotelController(ServicioHotel servicioHotel, ServicioHabitacion servicioHabitacion,
                           ServicioTipoHabitacion servicioTipoHabitacion, ServicioFiltroHoteles servicioFiltroHoteles,
                           HotelesMapper hotelesMapper) {
        this.servicioHotel = servicioHotel;
        this.servicioHabitacion = servicioHabitacion;
        this.servicioTipoHabitacion = servicioTipoHabitacion;
        this.servicioFiltroHoteles = servicioFiltroHoteles;
        this.hotelesMapper = hotelesMapper;
    }

    /**
     * Maneja la solicitud GET para mostrar la lista de hoteles.
     *
     * Obtiene una lista paginada de hoteles y la muestra en la vista correspondiente.
     * También se cargan los tipos de habitación disponibles para el filtrado.
     *
     * @param modelo el modelo de datos para la vista.
     * @param page   número de la página actual (por defecto 0).
     * @param size   tamaño de la página (por defecto 6).
     * @return el nombre de la vista para mostrar la lista de hoteles.
     */
    @GetMapping("/hoteles/lista")
    public String listaHoteles(Model modelo,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "6") int size) {
        modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());

        PaginaRespuestaHoteles<Hotel> hotelesPage = servicioHotel.buscarEntidadesPaginadas(page, size);
        List<Hotel> hoteles = hotelesPage.getContent();

        modelo.addAttribute("lista", hoteles);
        modelo.addAttribute("page", hotelesPage);

        return "hoteles/listaHoteles";
    }

    /**
     * Maneja la solicitud GET para mostrar los detalles de un hotel específico.
     *
     * Busca el hotel por su ID y carga sus detalles, incluyendo la imagen del hotel,
     * hoteles recomendados en la misma zona y los precios actuales de las habitaciones.
     *
     * @param modelo el modelo de datos para la vista.
     * @param id     el identificador del hotel.
     * @return el nombre de la vista para mostrar los detalles del hotel, o una vista de error si no se encuentra el hotel.
     */
    @GetMapping("/hotel/{id}")
    public String detallesHotel(Model modelo, @PathVariable Integer id) {
        Optional<Hotel> hotel = servicioHotel.encuentraPorId(id);

        if (hotel.isPresent()) {
            Optional<Imagen> optionalHotelImagen = hotel.get().getImagenesHotel().stream().findFirst();

            if (optionalHotelImagen.isPresent()) {
                String hotelImagen = optionalHotelImagen.get().getUrl();
                modelo.addAttribute("imagenHotel", hotelImagen);
            }

            modelo.addAttribute("recomendados", servicioHotel.obtenerHotelesEnTuZona(hotel.get()));
            modelo.addAttribute("hotel", hotel.get());
            modelo.addAttribute("preciosActuales", servicioHabitacion.obtenerPreciosActualesHabitacionesHotel(hotel.get()));

            return "hoteles/detallesHotel";
        } else {
            // Si no se encuentra el hotel, se muestra una página de error
            return "hotelNoEncontrado";
        }
    }

    /**
     * Maneja la solicitud GET para filtrar los hoteles basados en los criterios de búsqueda proporcionados.
     *
     * Aplica los filtros según los criterios (como categoría, tipo de habitación, capacidad y localización)
     * y muestra los resultados en la página de lista de hoteles.
     *
     * @param modelo   el modelo de datos para la vista.
     * @param criteria los criterios de búsqueda para filtrar los hoteles.
     * @return el nombre de la vista para mostrar la lista de hoteles filtrados.
     */
    @GetMapping("/filtrar-hoteles")
    public String filtrarHoteles(Model modelo, BusquedaCriteriaHoteles criteria) {
        // Establecer tamaño de página predeterminado si no está definido o es inválido
        if (criteria.getSize() == null || criteria.getSize() <= 0) {
            criteria.setSize(6); // Definir un valor predeterminado de tamaño de página
        }

        // Realizar búsqueda de hoteles con los criterios de filtro
        PaginaRespuestaHoteles<HotelDto> hoteles = servicioFiltroHoteles
                .buscarHoteles(hotelesMapper.filtrar(criteria), criteria.getPage(), criteria.getSize());

        // Añadir los resultados de la búsqueda y otros atributos al modelo
        modelo.addAttribute("page", hoteles);
        modelo.addAttribute("lista", hoteles.getContent());

        // Añadir los valores de los criterios de búsqueda al modelo para mantener los filtros en la vista
        modelo.addAttribute("categoria", criteria.getCategoria());
        modelo.addAttribute("tipoHabitacionId", criteria.getTipoHabitacionId());
        modelo.addAttribute("capacidadHabitacion", criteria.getCapacidadHabitacion());
        modelo.addAttribute("localizacionNombre", criteria.getLocalizacionNombre());

        // Cargar otros datos necesarios para la vista, como los tipos de habitación disponibles
        modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());

        return "hoteles/listaHoteles"; // Redirigir a la vista con los hoteles filtrados
    }
}
