package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
import com.eoi.grupo5.paginacion.PaginaRespuestaLocalizaciones;
import com.eoi.grupo5.servicios.*;
import com.eoi.grupo5.servicios.archivos.FileSystemStorageService;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/localizaciones")
public class AdminLocalizacionController {

    private final ServicioLocalizacion servicioLocalizacion;
    private final ServicioVuelo servicioVuelo;
    private final ServicioHotel servicioHotel;
    private final ServicioActividad servicioActividad;


    public AdminLocalizacionController(ServicioLocalizacion servicioLocalizacion, ServicioVuelo servicioVuelo, ServicioHotel servicioHotel, ServicioActividad servciActividad) {
        this.servicioLocalizacion = servicioLocalizacion;
        this.servicioVuelo = servicioVuelo;
        this.servicioHotel = servicioHotel;
        this.servicioActividad = servciActividad;
    }

    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaLocalizaciones<Localizacion> localizacionesPage = servicioLocalizacion.buscarEntidadesPaginadas(page, size);
        List<Localizacion> localizaciones = localizacionesPage.getContent();
        modelo.addAttribute("localizaciones",localizaciones);
        modelo.addAttribute("page", localizacionesPage);
        return "admin/adminLocalizaciones";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Localizacion> localizacion = servicioLocalizacion.encuentraPorId(id);
        if(localizacion.isPresent()) {
            modelo.addAttribute("localizacion",localizacion.get());
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());

            return "admin/adminDetallesLocalizacion";
        } else {
            // Hotel no encontrado - htlm
            return "localizacionNoEncontrada";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Localizacion localizacion = new Localizacion();
        modelo.addAttribute("localizacion", localizacion);
        modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
        modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
        modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
        return "admin/adminNuevaLocalizacion";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("localizacion") Localizacion localizacion, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            return "admin/adminNuevaLocalizacion";
        }
        try {
            servicioLocalizacion.guardar(localizacion);
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al crear la localización: " + e.getMessage());
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("actividades", servicioActividad.buscarEntidades());
            return "admin/adminNuevaLocalizacion";
        }
        return "redirect:/admin/localizaciones";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Localizacion> optionalLocalizacion = servicioLocalizacion.encuentraPorId(id);
        if(optionalLocalizacion.isPresent()) {
            servicioLocalizacion.eliminarPorId(id);
        }
        return "redirect:/admin/localizaciones";
    }

}
