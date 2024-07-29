package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import com.eoi.grupo5.paginacion.PaginaRespuestaPrecios;
import com.eoi.grupo5.servicios.*;
import com.eoi.grupo5.servicios.archivos.FileSystemStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/precios")
public class AdminPrecioController {


    private final ServicioPrecio servicioPrecio;
    private final ServicioHabitacion servicioHabitacion;
    private final ServicioAsiento servicioAsiento;
    private final ServicioActividad servicioActividad;

    public AdminPrecioController(ServicioPrecio servicioPrecio, ServicioHabitacion servicioHabitacion, ServicioAsiento servicioAsiento, ServicioActividad servicioActividad) {
        this.servicioPrecio = servicioPrecio;
        this.servicioHabitacion = servicioHabitacion;
        this.servicioAsiento = servicioAsiento;
        this.servicioActividad = servicioActividad;
    }

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
        return "admin/adminPrecios";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Precio> precio = servicioPrecio.encuentraPorId(id);
        if(precio.isPresent()) {
            modelo.addAttribute("precio",precio.get());
            return "admin/adminDetallesPrecio";
        } else {
            // Hotel no encontrado - htlm
            return "hotelNoEncontrado";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Precio precio = new Precio();
        modelo.addAttribute("precio", precio);
        modelo.addAttribute("habitaciones", servicioHabitacion.buscarEntidades());
        modelo.addAttribute("asientos", servicioAsiento.buscarEntidades());
        modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
        return "admin/adminNuevoPrecio";
    }

    @PostMapping("/crear")
    public String crear(
            @ModelAttribute("precio") Precio precio,
            @RequestParam(name = "habitacion.id", required = false) Integer habitacionId,
            @RequestParam(name = "asiento.id", required = false) Integer asientoId,
            @RequestParam(name = "actividad.id", required = false) Integer actividadId
    ) {
        try {
            // Manejo opcional para la Habitación
            if (habitacionId != null) {
                Habitacion habitacion = servicioHabitacion.encuentraPorId(habitacionId)
                        .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
                precio.setHabitacion(habitacion);
            } else {
                precio.setHabitacion(null); // Elimina la referencia si no se proporciona un ID
            }

            // Manejo opcional para el Asiento
            if (asientoId != null) {
                Asiento asiento = servicioAsiento.encuentraPorId(asientoId)
                        .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado"));
                precio.setAsiento(asiento);
            } else {
                precio.setAsiento(null); // Elimina la referencia si no se proporciona un ID
            }

            // Manejo opcional para la Actividad
            if (actividadId != null) {
                Actividad actividad = servicioActividad.encuentraPorId(actividadId)
                        .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
                precio.setActividad(actividad);
            } else {
                precio.setActividad(null); // Elimina la referencia si no se proporciona un ID
            }

            // Verifica que al menos una entidad esté asignada
            if (precio.getHabitacion() == null && precio.getAsiento() == null && precio.getActividad() == null) {
                throw new IllegalArgumentException("Debe proporcionar al menos uno de los IDs: habitacion.id, asiento.id, actividad.id");
            }

            // Guarda el objeto Precio
            servicioPrecio.guardar(precio);

        } catch (IllegalArgumentException e) {
            // Maneja excepciones específicas
            return "redirect:/admin/precios?error=" + e.getMessage();
        } catch (Exception e) {
            // Manejo general de errores
            return "redirect:/admin/precios?error=Ocurrió un error inesperado";
        }

        // Redirige a la página de lista de precios después de guardar
        return "redirect:/admin/precios";
    }


    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Precio> optionalPrecio = servicioPrecio.encuentraPorId(id);
        if(optionalPrecio.isPresent()) {
            servicioPrecio.eliminarPorId(id);
        }
        return "redirect:/admin/precios";
    }

}
