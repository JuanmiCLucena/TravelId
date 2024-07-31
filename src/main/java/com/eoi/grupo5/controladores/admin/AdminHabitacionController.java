package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
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
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaHabitaciones<Habitacion> habitacionesPage = servicioHabitacion.buscarEntidadesPaginadas(page, size);
        List<Habitacion> habitaciones = habitacionesPage.getContent();
        modelo.addAttribute("habitaciones",habitaciones);
        modelo.addAttribute("page", habitacionesPage);
        return "admin/habitaciones/adminHabitaciones";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Habitacion> habitacion = servicioHabitacion.encuentraPorId(id);
        if(habitacion.isPresent()) {
            modelo.addAttribute("habitacion",habitacion.get());
            modelo.addAttribute("precioActual",
                    servicioHabitacion.getPrecioActual(habitacion.get(), LocalDateTime.now()));
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());

        return "admin/habitaciones/adminDetallesHabitacion";
        } else {
            // Habitacion no encontrado - htlm
            return "habitacionNoEncontrado";
        }

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
    public String eliminar(@PathVariable Integer id) {
        Optional<Habitacion> optionalHabitacion = servicioHabitacion.encuentraPorId(id);
        if(optionalHabitacion.isPresent()) {
            servicioHabitacion.eliminarPorId(id);
        }
        return "redirect:/admin/habitaciones";
    }

}
