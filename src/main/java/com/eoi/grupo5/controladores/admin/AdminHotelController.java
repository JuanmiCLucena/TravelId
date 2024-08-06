package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.ServicioHotel;
import com.eoi.grupo5.servicios.ServicioImagen;
import com.eoi.grupo5.servicios.ServicioLocalizacion;
import com.eoi.grupo5.servicios.archivos.FileSystemStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
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
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaHoteles<Hotel> hotelesPage = servicioHotel.buscarEntidadesPaginadas(page, size);
        List<Hotel> hoteles = hotelesPage.getContent();
        modelo.addAttribute("hoteles", hoteles);
        modelo.addAttribute("page", hotelesPage);
        return "admin/hoteles/adminHoteles";
    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Hotel hotel = new Hotel();
        modelo.addAttribute("hotel", hotel);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        return "admin/hoteles/adminNuevoHotel";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam(name = "imagen") MultipartFile imagen,
            @Valid @ModelAttribute("hotel") Hotel hotel,
            BindingResult result,
            Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            return "admin/hoteles/adminNuevoHotel";
        }

        try {
            servicioHotel.guardar(hotel);
            Imagen imagenBD = new Imagen();
            imagenBD.setHotel(hotel);
            imagenBD.setUrl(String.valueOf(hotel.getId()));
            servicioImagen.guardar(imagenBD);
            String FILE_NAME = "hotel-" + hotel.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
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

    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(@PathVariable Integer id, Model modelo) {
        Optional<Hotel> hotel = servicioHotel.encuentraPorId(id);
        if (hotel.isPresent()) {
            modelo.addAttribute("hotel", hotel.get());
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            return "admin/hoteles/adminDetallesHotel";
        } else {
            modelo.addAttribute("error", "Hotel no encontrado");
            return "error/paginaError";
        }
    }

    @PutMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @RequestParam(name = "imagen", required = false) MultipartFile imagen,
            @Valid @ModelAttribute("hotel") Hotel hotel,
            BindingResult result,
            Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            return "admin/hoteles/adminDetallesHotel";
        }

        try {
            Optional<Hotel> hotelOptional = servicioHotel.encuentraPorId(id);
            if (hotelOptional.isPresent()) {
                Hotel hotelExistente = hotelOptional.get();
                hotelExistente.setNombre(hotel.getNombre());
                hotelExistente.setDescripcion(hotel.getDescripcion());
                hotelExistente.setLocalizacion(hotel.getLocalizacion());

                if (imagen != null && !imagen.isEmpty()) {
                    Imagen imagenBD = new Imagen();
                    imagenBD.setHotel(hotelExistente);
                    imagenBD.setUrl(String.valueOf(hotelExistente.getId()));
                    servicioImagen.guardar(imagenBD);
                    String FILE_NAME = "hotel-" + hotelExistente.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
                    imagenBD.setUrl(FILE_NAME);
                    hotelExistente.getImagenesHotel().clear();
                    fileSystemStorageService.store(imagen, FILE_NAME);
                    hotelExistente.getImagenesHotel().add(imagenBD);
                }

                servicioHotel.guardar(hotelExistente);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/hoteles";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Hotel> optionalHotel = servicioHotel.encuentraPorId(id);
        if (optionalHotel.isPresent()) {
            servicioHotel.eliminarPorId(id);
        }
        return "redirect:/admin/hoteles";
    }
}
