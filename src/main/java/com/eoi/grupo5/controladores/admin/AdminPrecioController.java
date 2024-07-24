package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.modelos.TipoHabitacion;
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
            @RequestParam("habitacion.id") Integer habitacionId,
            @RequestParam("asiento.id") Integer asientoId,
            @RequestParam("actividad.id") Integer actividadId
    ) {

        try {

            Hotel hotel = servicioHotel.encuentraPorId(hotelId).get();
            habitacion.setHotel(hotel);

            TipoHabitacion tipo = servicioTipoHabitacion.encuentraPorId(tipoId).get();
            habitacion.setTipo(tipo);

          servicioPrecio.guardar(precio);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/hoteles";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Hotel> optionalHotel = servicioHotel.encuentraPorId(id);
        if(optionalHotel.isPresent()) {
            servicioHotel.eliminarPorId(id);
        }
        return "redirect:/admin/hoteles";
    }

}
