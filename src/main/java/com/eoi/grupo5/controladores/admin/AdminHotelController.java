package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.dtos.HotelFormDto;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Localizacion;
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

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    // Mostrar página de creación de hotel
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        HotelFormDto hotelFormDto = new HotelFormDto();
        modelo.addAttribute("hotelFormDto", hotelFormDto);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        return "admin/hoteles/adminNuevoHotel";
    }

    @PostMapping("/crear")
    public String crear(
            @Valid @ModelAttribute("hotelFormDto") HotelFormDto hotelFormDto,
            BindingResult result,
            Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            return "admin/hoteles/adminNuevoHotel";
        }

        try {
            // Crear el hotel
            Hotel hotel = new Hotel();
            hotel.setNombre(hotelFormDto.getNombre());
            hotel.setCategoria(hotelFormDto.getCategoria());
            hotel.setDescripcion(hotelFormDto.getDescripcion());
            hotel.setContacto(hotelFormDto.getContacto());

            Optional<Localizacion> optLocalizacion = servicioLocalizacion.encuentraPorId(hotelFormDto.getLocalizacionId());
            optLocalizacion.ifPresent(hotel::setLocalizacion);

            // Guardar el hotel sin imágenes primero
            Hotel hotelGuardado = servicioHotel.guardar(hotel);

            // Procesar las imágenes
            if (hotelFormDto.getImagenes() != null) {
                for (MultipartFile archivo : hotelFormDto.getImagenes()) {
                    if (!archivo.isEmpty()) {
                        Imagen imagenBD = new Imagen();
                        imagenBD.setHotel(hotelGuardado);
                        String FILE_NAME = "hotel-" + hotelGuardado.getId() + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(archivo.getOriginalFilename());
                        imagenBD.setUrl(FILE_NAME);

                        // Almacenar la imagen en el sistema de archivos
                        fileSystemStorageService.store(archivo, FILE_NAME);

                        // Agregar la imagen al hotel
                        hotelGuardado.getImagenesHotel().add(imagenBD);

                        // Guardar la imagen en la base de datos
                        servicioImagen.guardar(imagenBD);
                    }
                }
            }

            // Guardar el hotel con las nuevas imágenes
            servicioHotel.guardar(hotelGuardado);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/hoteles";
    }

    // Mostrar página de edición de hotel
    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(@PathVariable Integer id, Model modelo) {
        Optional<Hotel> hotelOptional = servicioHotel.encuentraPorId(id);
        if (hotelOptional.isPresent()) {
            Hotel hotel = hotelOptional.get();

            HotelFormDto hotelFormDto = new HotelFormDto();
            hotelFormDto.setId(hotel.getId());
            hotelFormDto.setNombre(hotel.getNombre());
            hotelFormDto.setCategoria(hotel.getCategoria());
            hotelFormDto.setDescripcion(hotel.getDescripcion());
            hotelFormDto.setContacto(hotel.getContacto());
            hotelFormDto.setLocalizacionId(hotel.getLocalizacion().getId());

            modelo.addAttribute("hotelFormDto", hotelFormDto);
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("imagenesHotel", hotel.getImagenesHotel());

            return "admin/hoteles/adminDetallesHotel";
        } else {
            return "error/paginaError";
        }
    }

    // Editar hotel existente
    @PutMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @RequestParam(name = "imagenes", required = false) MultipartFile[] imagenes,
            @Valid @ModelAttribute("hotelFormDto") HotelFormDto hotelFormDto,
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

                hotelExistente.setNombre(hotelFormDto.getNombre());
                hotelExistente.setCategoria(hotelFormDto.getCategoria());
                hotelExistente.setDescripcion(hotelFormDto.getDescripcion());
                hotelExistente.setContacto(hotelFormDto.getContacto());
                hotelExistente.setLocalizacion(servicioLocalizacion.encuentraPorId(hotelFormDto.getLocalizacionId()).orElse(null));

                if (imagenes != null && imagenes.length > 0) {
                    boolean algunaImagenNueva = false;

                    for (MultipartFile archivo : imagenes) {
                        if (!archivo.isEmpty()) {
                            algunaImagenNueva = true;
                            break;
                        }
                    }

                    if (algunaImagenNueva) {
                        hotelExistente.getImagenesHotel().clear();

                        for (MultipartFile archivo : imagenes) {
                            if (!archivo.isEmpty()) {
                                Imagen imagenBD = new Imagen();
                                imagenBD.setHotel(hotelExistente);

                                String FILE_NAME = "hotel-" + hotelExistente.getId() + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(archivo.getOriginalFilename());
                                imagenBD.setUrl(FILE_NAME);

                                fileSystemStorageService.store(archivo, FILE_NAME);

                                hotelExistente.getImagenesHotel().add(imagenBD);
                                servicioImagen.guardar(imagenBD);
                            }
                        }
                    }
                }

                servicioHotel.guardar(hotelExistente);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar el hotel", e);
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
