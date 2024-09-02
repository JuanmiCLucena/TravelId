package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.paginacion.PaginaRespuestaLocalizaciones;
import com.eoi.grupo5.servicios.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar las operaciones de administración relacionadas con las localizaciones.
 * Permite crear, listar, editar y eliminar localizaciones desde la interfaz de administración.
 */
@Controller
@RequestMapping("/admin/localizaciones")
public class AdminLocalizacionController {

    private final ServicioLocalizacion servicioLocalizacion;
    private final ServicioVuelo servicioVuelo;
    private final ServicioHotel servicioHotel;
    private final ServicioActividad servicioActividad;
    private final ServicioPais servicioPais;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param servicioLocalizacion Servicio para manejar las operaciones de localización.
     * @param servicioVuelo Servicio para manejar las operaciones de vuelo.
     * @param servicioHotel Servicio para manejar las operaciones de hotel.
     * @param servicioActividad Servicio para manejar las operaciones de actividad.
     * @param servicioPais Servicio para manejar las operaciones de país.
     */
    public AdminLocalizacionController(ServicioLocalizacion servicioLocalizacion, ServicioVuelo servicioVuelo, ServicioHotel servicioHotel, ServicioActividad servicioActividad, ServicioPais servicioPais) {
        this.servicioLocalizacion = servicioLocalizacion;
        this.servicioVuelo = servicioVuelo;
        this.servicioHotel = servicioHotel;
        this.servicioActividad = servicioActividad;
        this.servicioPais = servicioPais;
    }

    /**
     * Maneja la solicitud GET para listar todas las localizaciones paginadas.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param page Número de la página solicitada (por defecto 0).
     * @param size Tamaño de la página (por defecto 10).
     * @return El nombre de la plantilla que muestra la lista de localizaciones.
     */
    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaLocalizaciones<Localizacion> localizacionesPage = servicioLocalizacion.buscarEntidadesPaginadas(page, size);
        List<Localizacion> localizaciones = localizacionesPage.getContent();
        modelo.addAttribute("localizaciones", localizaciones);
        modelo.addAttribute("page", localizacionesPage);
        return "admin/localizaciones/adminLocalizaciones";
    }

    /**
     * Muestra la página para crear una nueva localización.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra el formulario de creación de localización.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Localizacion localizacion = new Localizacion();
        modelo.addAttribute("localizacion", localizacion);
        modelo.addAttribute("paises", servicioPais.buscarEntidades());
        return "admin/localizaciones/adminNuevaLocalizacion";
    }

    /**
     * Maneja la solicitud POST para crear una nueva localización.
     *
     * @param localizacion La localización que se va a crear, validada.
     * @param result Resultado de la validación de la localización.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de localizaciones o la vista de creación si hay errores.
     */
    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("localizacion") Localizacion localizacion, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("paises", servicioPais.buscarEntidades());
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            return "admin/localizaciones/adminNuevaLocalizacion";
        }
        try {
            servicioLocalizacion.guardar(localizacion);
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al crear la localización: " + e.getMessage());
            modelo.addAttribute("paises", servicioPais.buscarEntidades());
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            return "admin/localizaciones/adminNuevaLocalizacion";
        }
        return "redirect:/admin/localizaciones";
    }

    /**
     * Muestra la página para editar una localización existente.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param id ID de la localización que se va a editar.
     * @return El nombre de la plantilla que muestra el formulario de edición de localización, o una página de error si no se encuentra la localización.
     */
    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(Model modelo, @PathVariable Integer id) {
        Optional<Localizacion> localizacion = servicioLocalizacion.encuentraPorId(id);
        if (localizacion.isPresent()) {
            modelo.addAttribute("localizacion", localizacion.get());
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            modelo.addAttribute("paises", servicioPais.buscarEntidades());
            return "admin/localizaciones/adminDetallesLocalizacion";
        } else {
            // Localización no encontrada - página de error
            return "error/paginaError";
        }
    }

    /**
     * Maneja la solicitud PUT para editar una localización existente.
     *
     * @param id ID de la localización que se va a actualizar.
     * @param localizacion La localización actualizada, validada.
     * @param result Resultado de la validación de la localización.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de localizaciones o la vista de edición si hay errores.
     */
    @PutMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, @Valid @ModelAttribute("localizacion") Localizacion localizacion, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            modelo.addAttribute("paises", servicioPais.buscarEntidades());
            return "admin/localizaciones/adminDetallesLocalizacion";
        }
        try {
            localizacion.setId(id);
            servicioLocalizacion.guardar(localizacion);
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al actualizar la localización: " + e.getMessage());
            modelo.addAttribute("paises", servicioPais.buscarEntidades());
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            return "admin/localizaciones/adminDetallesLocalizacion";
        }
        return "redirect:/admin/localizaciones";
    }

    /**
     * Maneja la solicitud DELETE para eliminar una localización existente.
     *
     * @param id ID de la localización que se va a eliminar.
     * @return Redirección a la lista de localizaciones.
     */
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Localizacion> optionalLocalizacion = servicioLocalizacion.encuentraPorId(id);
        if (optionalLocalizacion.isPresent()) {
            servicioLocalizacion.eliminarPorId(id);
        }
        return "redirect:/admin/localizaciones";
    }

}
