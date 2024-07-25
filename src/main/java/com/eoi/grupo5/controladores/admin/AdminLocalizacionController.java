package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.modelos.Vuelo;
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
    public String listar(Model modelo) {
        List<Localizacion> localizaciones = servicioLocalizacion.buscarEntidades();
        modelo.addAttribute("localizaciones",localizaciones);
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
    public String crear(@ModelAttribute("localizacion") Localizacion localizacion) {

        try {
            servicioLocalizacion.guardar(localizacion);
        } catch (Exception e) {
            throw new RuntimeException(e);
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