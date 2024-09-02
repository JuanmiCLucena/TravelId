package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaPrecios;
import com.eoi.grupo5.servicios.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar las operaciones de administración relacionadas con los precios.
 * Permite crear, listar, editar y eliminar precios desde la interfaz de administración.
 */
@Controller
@RequestMapping("/admin/precios")
public class AdminPrecioController {


    private final ServicioPrecio servicioPrecio;
    private final ServicioHabitacion servicioHabitacion;
    private final ServicioAsiento servicioAsiento;
    private final ServicioActividad servicioActividad;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param servicioPrecio Servicio para manejar las operaciones de precio.
     * @param servicioHabitacion Servicio para manejar las operaciones de habitación.
     * @param servicioAsiento Servicio para manejar las operaciones de asiento.
     * @param servicioActividad Servicio para manejar las operaciones de actividad.
     */
    public AdminPrecioController(ServicioPrecio servicioPrecio, ServicioHabitacion servicioHabitacion, ServicioAsiento servicioAsiento, ServicioActividad servicioActividad) {
        this.servicioPrecio = servicioPrecio;
        this.servicioHabitacion = servicioHabitacion;
        this.servicioAsiento = servicioAsiento;
        this.servicioActividad = servicioActividad;
    }

    /**
     * Maneja la solicitud GET para listar todos los precios paginados.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param page Número de la página solicitada (por defecto 0).
     * @param size Tamaño de la página (por defecto 10).
     * @return El nombre de la plantilla que muestra la lista de precios.
     */
    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaPrecios<Precio> preciosPage = servicioPrecio.buscarEntidadesPaginadas(page, size);
        List<Precio> precios = preciosPage.getContent();
        modelo.addAttribute("precios",precios);
        modelo.addAttribute("page", preciosPage);
        return "admin/precios/adminPrecios";
    }

    /**
     * Muestra la página para crear un nuevo precio.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra el formulario de creación de precio.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Precio precio = new Precio();
        modelo.addAttribute("precio", precio);
        modelo.addAttribute("habitaciones", servicioHabitacion.buscarEntidades());
        modelo.addAttribute("asientos", servicioAsiento.buscarEntidades());
        modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
        return "admin/precios/adminNuevoPrecio";
    }

    /**
     * Maneja la solicitud POST para crear un nuevo precio.
     *
     * @param precio El precio que se va a crear, validado.
     * @param bindingResult Resultado de la validación del precio.
     * @param habitacionId ID de la habitación asociada (opcional).
     * @param asientoId ID del asiento asociado (opcional).
     * @param actividadId ID de la actividad asociada (opcional).
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de precios o la vista de creación si hay errores.
     */
    @PostMapping("/crear")
    public String crear(
            @Valid @ModelAttribute("precio") Precio precio,
            BindingResult bindingResult,
            @RequestParam(name = "habitacion.id", required = false) Integer habitacionId,
            @RequestParam(name = "asiento.id", required = false) Integer asientoId,
            @RequestParam(name = "actividad.id", required = false) Integer actividadId,
            Model modelo
    ) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("habitaciones", servicioHabitacion.buscarEntidades());
            modelo.addAttribute("asientos", servicioAsiento.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            return "admin/precios/adminNuevoPrecio";
        }

        try {
            if (habitacionId != null) {
                Habitacion habitacion = servicioHabitacion.encuentraPorId(habitacionId)
                        .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
                precio.setHabitacion(habitacion);
            } else {
                precio.setHabitacion(null);
            }

            if (asientoId != null) {
                Asiento asiento = servicioAsiento.encuentraPorId(asientoId)
                        .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado"));
                precio.setAsiento(asiento);
            } else {
                precio.setAsiento(null);
            }

            if (actividadId != null) {
                Actividad actividad = servicioActividad.encuentraPorId(actividadId)
                        .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
                precio.setActividad(actividad);
            } else {
                precio.setActividad(null);
            }

            if (precio.getHabitacion() == null && precio.getAsiento() == null && precio.getActividad() == null) {
                throw new IllegalArgumentException("Debe proporcionar al menos uno de los IDs: habitacion.id, asiento.id, actividad.id");
            }

            servicioPrecio.guardar(precio);

        } catch (IllegalArgumentException e) {
            return "redirect:/admin/precios?error=" + e.getMessage();
        } catch (Exception e) {
            return "redirect:/admin/precios?error=Ocurrió un error inesperado";
        }

        return "redirect:/admin/precios";
    }

    /**
     * Muestra la página para editar un precio existente.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param id ID del precio que se va a editar.
     * @return El nombre de la plantilla que muestra el formulario de edición de precio, o una página de error si no se encuentra el precio.
     */
    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(Model modelo, @PathVariable Integer id) {
        Optional<Precio> precio = servicioPrecio.encuentraPorId(id);
        if(precio.isPresent()) {
            modelo.addAttribute("precio",precio.get());
            return "admin/precios/adminDetallesPrecio";
        } else {
            // Hotel no encontrado - htlm
            return "error/paginaError";
        }

    }

    /**
     * Maneja la solicitud PUT para editar un precio existente.
     *
     * @param id ID del precio que se va a actualizar.
     * @param precio El precio actualizado, validado.
     * @param bindingResult Resultado de la validación del precio.
     * @param habitacionId ID de la habitación asociada (opcional).
     * @param asientoId ID del asiento asociado (opcional).
     * @param actividadId ID de la actividad asociada (opcional).
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de precios o la vista de edición si hay errores.
     */
    @PutMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @Valid @ModelAttribute("precio") Precio precio,
            BindingResult bindingResult,
            @RequestParam(name = "habitacion.id", required = false) Integer habitacionId,
            @RequestParam(name = "asiento.id", required = false) Integer asientoId,
            @RequestParam(name = "actividad.id", required = false) Integer actividadId,
            Model modelo
    ) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("habitaciones", servicioHabitacion.buscarEntidades());
            modelo.addAttribute("asientos", servicioAsiento.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            return "admin/precios/adminDetallesPrecio";
        }

        try {
            if (habitacionId != null) {
                Habitacion habitacion = servicioHabitacion.encuentraPorId(habitacionId)
                        .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
                precio.setHabitacion(habitacion);
            } else {
                precio.setHabitacion(null);
            }

            if (asientoId != null) {
                Asiento asiento = servicioAsiento.encuentraPorId(asientoId)
                        .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado"));
                precio.setAsiento(asiento);
            } else {
                precio.setAsiento(null);
            }

            if (actividadId != null) {
                Actividad actividad = servicioActividad.encuentraPorId(actividadId)
                        .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
                precio.setActividad(actividad);
            } else {
                precio.setActividad(null);
            }

            if (precio.getHabitacion() == null && precio.getAsiento() == null && precio.getActividad() == null) {
                throw new IllegalArgumentException("Debe proporcionar al menos uno de los IDs: habitacion.id, asiento.id, actividad.id");
            }

            servicioPrecio.guardar(precio);

        } catch (IllegalArgumentException e) {
            return "redirect:/admin/precios?error=" + e.getMessage();
        } catch (Exception e) {
            return "redirect:/admin/precios?error=Ocurrió un error inesperado";
        }

        return "redirect:/admin/precios";
    }

    /**
     * Maneja la solicitud DELETE para eliminar un precio existente.
     *
     * @param id ID del precio que se va a eliminar.
     * @return Redirección a la lista de precios.
     */
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Precio> optionalPrecio = servicioPrecio.encuentraPorId(id);
        if(optionalPrecio.isPresent()) {
            servicioPrecio.eliminarPorId(id);
        }
        return "redirect:/admin/precios";
    }
}
