package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
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
@RequestMapping("/admin/habitaciones")
public class AdminHabitacionController {

    private final ServicioHabitacion servicioHabitacion;
    private final ServicioTipoHabitacion servicioTipoHabitacion;
    private final ServicioHotel servicioHotel;
    private final ServicioImagen servicioImagen;
    private final FileSystemStorageService fileSystemStorageService;

    public AdminHabitacionController(ServicioHabitacion servicioHabitacion, ServicioTipoHabitacion servicioTipoHabitacion, ServicioHotel servicioHotel, ServicioImagen servicioImagen, FileSystemStorageService fileSystemStorageService) {
        this.servicioHabitacion = servicioHabitacion;
        this.servicioTipoHabitacion = servicioTipoHabitacion;
        this.servicioHotel = servicioHotel;
        this.servicioImagen = servicioImagen;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaHabitaciones<Habitacion> habitacionesPage = servicioHabitacion.buscarEntidadesPaginadas(page, size);
        List<Habitacion> habitaciones = habitacionesPage.getContent();
        modelo.addAttribute("habitaciones", habitaciones);
        modelo.addAttribute("page", habitacionesPage);
        return "admin/habitaciones/adminHabitaciones";
    }
    
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Habitacion habitacion = new Habitacion();
        modelo.addAttribute("habitacion", habitacion);
        modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
        modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
        return "admin/habitaciones/adminNuevaHabitacion";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam(name = "imagen") MultipartFile imagen,
            @Valid @ModelAttribute("habitacion") Habitacion habitacion,
            BindingResult bindingResult,
            Model modelo,
            @RequestParam("tipo.id") Integer tipoId,
            @RequestParam("hotel.id") Integer hotelId
    ) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
            return "admin/habitaciones/adminNuevaHabitacion";
        }

        try {
            Optional<Hotel> hotelOpt = servicioHotel.encuentraPorId(hotelId);
            if (hotelOpt.isPresent()) {
                habitacion.setHotel(hotelOpt.get());
            } else {
                modelo.addAttribute("error", "Hotel no encontrado");
                return "admin/habitaciones/adminNuevaHabitacion";
            }

            Optional<TipoHabitacion> tipoOpt = servicioTipoHabitacion.encuentraPorId(tipoId);
            if (tipoOpt.isPresent()) {
                habitacion.setTipo(tipoOpt.get());
            } else {
                modelo.addAttribute("error", "Tipo de habitación no encontrado");
                return "admin/habitaciones/adminNuevaHabitacion";
            }

            servicioHabitacion.guardar(habitacion);

            Imagen imagenBD = new Imagen();
            imagenBD.setHabitacionImagen(habitacion);
            imagenBD.setUrl(String.valueOf(habitacion.getId()));
            servicioImagen.guardar(imagenBD);

            String FILE_NAME = "habitacion-" + habitacion.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
            imagenBD.setUrl(FILE_NAME);
            fileSystemStorageService.store(imagen, FILE_NAME);

            habitacion.getImagenesHabitacion().clear();
            habitacion.getImagenesHabitacion().add(imagenBD);
            servicioHabitacion.guardar(habitacion);
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al guardar la habitación");
            return "admin/habitaciones/adminNuevaHabitacion";
        }

        return "redirect:/admin/habitaciones";
    }

    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(@PathVariable Integer id, Model modelo) {
        Optional<Habitacion> habitacion = servicioHabitacion.encuentraPorId(id);
        if (habitacion.isPresent()) {
            modelo.addAttribute("habitacion", habitacion.get());
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
            return "admin/habitaciones/adminDetallesHabitacion";
        } else {
            return "admin/habitaciones/habitacionNoEncontrado";
        }
    }

    @PutMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @RequestParam(name = "imagen", required = false) MultipartFile imagen,
            @Valid @ModelAttribute("habitacion") Habitacion habitacion,
            BindingResult bindingResult,
            Model modelo,
            @RequestParam("tipo.id") Integer tipoId,
            @RequestParam("hotel.id") Integer hotelId
    ) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
            return "admin/habitaciones/adminDetallesHabitacion";
        }

        try {
            Optional<Hotel> hotelOpt = servicioHotel.encuentraPorId(hotelId);
            if (hotelOpt.isPresent()) {
                habitacion.setHotel(hotelOpt.get());
            } else {
                modelo.addAttribute("error", "Hotel no encontrado");
                return "admin/habitaciones/adminDetallesHabitacion";
            }

            Optional<TipoHabitacion> tipoOpt = servicioTipoHabitacion.encuentraPorId(tipoId);
            if (tipoOpt.isPresent()) {
                habitacion.setTipo(tipoOpt.get());
            } else {
                modelo.addAttribute("error", "Tipo de habitación no encontrado");
                return "admin/habitaciones/adminDetallesHabitacion";
            }

            servicioHabitacion.guardar(habitacion);

            if (imagen != null && !imagen.isEmpty()) {
                Imagen imagenBD = new Imagen();
                imagenBD.setHabitacionImagen(habitacion);
                imagenBD.setUrl(String.valueOf(habitacion.getId()));
                servicioImagen.guardar(imagenBD);

                String FILE_NAME = "habitacion-" + habitacion.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
                imagenBD.setUrl(FILE_NAME);
                fileSystemStorageService.store(imagen, FILE_NAME);

                habitacion.getImagenesHabitacion().clear();
                habitacion.getImagenesHabitacion().add(imagenBD);
                servicioHabitacion.guardar(habitacion);
            }
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al editar la habitación");
            return "admin/habitaciones/adminDetallesHabitacion";
        }

        return "redirect:/admin/habitaciones";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Model modelo) {
        Optional<Habitacion> optionalHabitacion = servicioHabitacion.encuentraPorId(id);
        if (optionalHabitacion.isPresent()) {
            servicioHabitacion.eliminarPorId(id);
        } else {
            modelo.addAttribute("error", "Habitación no encontrada");
            return "admin/habitaciones/adminHabitaciones";
        }
        return "redirect:/admin/habitaciones";
    }
}
