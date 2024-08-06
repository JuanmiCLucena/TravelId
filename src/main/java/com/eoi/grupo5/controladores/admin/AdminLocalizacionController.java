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

@Controller
@RequestMapping("/admin/localizaciones")
public class AdminLocalizacionController {

    private final ServicioLocalizacion servicioLocalizacion;
    private final ServicioVuelo servicioVuelo;
    private final ServicioHotel servicioHotel;
    private final ServicioActividad servicioActividad;
    private final ServicioPais servicioPais;

    public AdminLocalizacionController(ServicioLocalizacion servicioLocalizacion, ServicioVuelo servicioVuelo, ServicioHotel servicioHotel, ServicioActividad servicioActividad, ServicioPais servicioPais) {
        this.servicioLocalizacion = servicioLocalizacion;
        this.servicioVuelo = servicioVuelo;
        this.servicioHotel = servicioHotel;
        this.servicioActividad = servicioActividad;
        this.servicioPais = servicioPais;
    }

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

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Localizacion localizacion = new Localizacion();
        modelo.addAttribute("localizacion", localizacion);
        modelo.addAttribute("paises", servicioPais.buscarEntidades());
        return "admin/localizaciones/adminNuevaLocalizacion";
    }

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

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Localizacion> optionalLocalizacion = servicioLocalizacion.encuentraPorId(id);
        if (optionalLocalizacion.isPresent()) {
            servicioLocalizacion.eliminarPorId(id);
        }
        return "redirect:/admin/localizaciones";
    }

}
