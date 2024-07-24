package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.TipoHabitacion;
import com.eoi.grupo5.servicios.*;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.archivos.FileSystemStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    public String listaHabitaciones(Model modelo) {
        List<Habitacion> habitaciones = servicioHabitacion.buscarEntidades();
        modelo.addAttribute("habitaciones",habitaciones);
        return "admin/adminHabitaciones";
    }

    @GetMapping("/{id}")
    public String detallesHabitacion(Model modelo, @PathVariable Integer id) {
        Optional<Habitacion> habitacion = servicioHabitacion.encuentraPorId(id);
        // Si no encontramos la habitacion no hemos encontrado la habitacion
        if(habitacion.isPresent()) {
            modelo.addAttribute("habitacion",habitacion.get());
            modelo.addAttribute("precioActual",
                    servicioHabitacion.getPrecioActual(habitacion.get(), LocalDateTime.now()));
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());

        return "admin/detallesHabitacion";
        } else {
            // Habitacion no encontrado - htlm
            return "habitacionNoEncontrado";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrearHabitacion(Model modelo) {
        Habitacion habitacion = new Habitacion();
        modelo.addAttribute("habitacion", habitacion);
        modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
        modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
        return "admin/nuevaHabitacion";
    }

    @PostMapping("/crear")
    public String crearHabitacion(
            @RequestParam(name = "imagen") MultipartFile imagen,
            @ModelAttribute("habitacion") Habitacion habitacion,
            @RequestParam("tipo.id") Integer tipoId,
            @RequestParam("hotel.id") Integer hotelId
            ) {

        try {

            String FILE_NAME;

            Hotel hotel = servicioHotel.encuentraPorId(hotelId).get();
            habitacion.setHotel(hotel);

            TipoHabitacion tipo = servicioTipoHabitacion.encuentraPorId(tipoId).get();
            habitacion.setTipo(tipo);

            servicioHabitacion.guardar(habitacion);
            Imagen imagenBD = new Imagen();
            imagenBD.setHabitacionImagen(habitacion);
            imagenBD.setUrl(String.valueOf(habitacion.getId()));
            servicioImagen.guardar(imagenBD);
            FILE_NAME = "habitacion-" + habitacion.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
            imagenBD.setUrl(FILE_NAME);
            habitacion.getImagenesHabitacion().clear();
            fileSystemStorageService.store(imagen, FILE_NAME);

            habitacion.getImagenesHabitacion().add(imagenBD);
            servicioHabitacion.guardar(habitacion);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/habitaciones";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminarHabitacion(@PathVariable Integer id) {
        Optional<Habitacion> optionalHabitacion = servicioHabitacion.encuentraPorId(id);
        if(optionalHabitacion.isPresent()) {
            servicioHabitacion.eliminarPorId(id);
        }
        return "redirect:/admin/habitaciones";
    }

}
