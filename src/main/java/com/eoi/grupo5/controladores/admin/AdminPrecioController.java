package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
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
    public String listar(Model modelo) {
        List<Precio> precios = servicioPrecio.buscarEntidades();
        modelo.addAttribute("precios",precios);
        return "admin/adminPrecios";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Precio> precio = servicioPrecio.encuentraPorId(id);
        // Si no encontramos el hotel no hemos encontrado el hotel
        if(precio.isPresent()) {
            modelo.addAttribute("precio",precio.get());
            modelo.addAttribute("habitaciones", servicioHabitacion.buscarEntidades());
            modelo.addAttribute("asientos", servicioAsiento.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());

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
            @RequestParam(name = "habitacion.id") Integer habitacionId,
            @RequestParam(name = "asiento.id") Integer asientoId,
            @RequestParam(name = "actividad.id") Integer actividadId
    ) {

        try {
            boolean assigned = false;

            if (habitacionId != null) {
                Habitacion habitacion = servicioHabitacion.encuentraPorId(habitacionId)
                        .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
                precio.setHabitacion(habitacion);
                assigned = true;
            }

            if (asientoId != null) {
                Asiento asiento = servicioAsiento.encuentraPorId(asientoId)
                        .orElseThrow(() -> new IllegalArgumentException("Asiento no encontrado"));
                precio.setAsiento(asiento);
                assigned = true;
            }

            if (actividadId != null) {
                Actividad actividad = servicioActividad.encuentraPorId(actividadId)
                        .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
                precio.setActividad(actividad);
                assigned = true;
            }

            if (!assigned) {
                throw new IllegalArgumentException("Debe proporcionar al menos uno de los IDs: habitacion.id, asiento.id, actividad.id");
            }

            servicioPrecio.guardar(precio);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
