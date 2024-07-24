package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.ServicioHotel;
import com.eoi.grupo5.servicios.ServicioImagen;
import com.eoi.grupo5.servicios.ServicioLocalizacion;
import com.eoi.grupo5.servicios.archivos.FileSystemStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/hoteles")
public class AdminHotelController {

    private final ServicioHotel servicioHotel;
    private final ServicioHabitacion servicioHabitacion;
    private final ServicioImagen servicioImagen;
    private final ServicioLocalizacion servicioLocalizacion;

    private final FileSystemStorageService fileSystemStorageService;


    public AdminHotelController(ServicioHotel servicioHotel, ServicioHabitacion servicioHabitacion, ServicioImagen servicioImagen, ServicioLocalizacion servicioLocalizacion, FileSystemStorageService fileSystemStorageService) {
        this.servicioHotel = servicioHotel;
        this.servicioHabitacion = servicioHabitacion;
        this.servicioImagen = servicioImagen;
        this.servicioLocalizacion = servicioLocalizacion;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @GetMapping
    public String listaHoteles(Model modelo) {
        List<Hotel> hoteles = servicioHotel.buscarEntidades();
        modelo.addAttribute("hoteles",hoteles);
        return "admin/adminHoteles";
    }

    @GetMapping("/{id}")
    public String detallesHotel(Model modelo, @PathVariable Integer id) {
        Optional<Hotel> hotel = servicioHotel.encuentraPorId(id);
        // Si no encontramos el hotel no hemos encontrado el hotel
        if(hotel.isPresent()) {
            modelo.addAttribute("hotel",hotel.get());
            modelo.addAttribute("preciosActuales",
                    servicioHabitacion.obtenerPreciosActualesHabitacionesHotel(hotel.get()));
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());

        return "admin/detallesHotel";
        } else {
            // Hotel no encontrado - htlm
            return "hotelNoEncontrado";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrearHotel(Model modelo) {
        Hotel hotel = new Hotel();
        modelo.addAttribute("hotel", hotel);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        return "admin/nuevoHotel";
    }

    @PostMapping("/crear")
    public String crearHotel(@RequestParam(name = "imagen") MultipartFile imagen, @ModelAttribute("hotel") Hotel hotel) {

        try {

            String FILE_NAME;

            servicioHotel.guardar(hotel);
            Imagen imagenBD = new Imagen();
            imagenBD.setHotel(hotel);
            imagenBD.setUrl(String.valueOf(hotel.getId()));
            servicioImagen.guardar(imagenBD);
            FILE_NAME = "hotel-" + hotel.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
            imagenBD.setUrl(FILE_NAME);
            hotel.getImagenesHotel().clear();
            fileSystemStorageService.store(imagen, FILE_NAME);

            hotel.getImagenesHotel().add(imagenBD);
            servicioHotel.guardar(hotel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/hoteles";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminarHotel(@PathVariable Integer id) {
        Optional<Hotel> optionalHotel = servicioHotel.encuentraPorId(id);
        if(optionalHotel.isPresent()) {
            servicioHotel.eliminarPorId(id);
        }
        return "redirect:/admin/hoteles";
    }

}
