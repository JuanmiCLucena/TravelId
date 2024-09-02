package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.dtos.HabitacionFormDto;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar las operaciones relacionadas con las habitaciones en la sección de administración.
 * Proporciona funcionalidades CRUD (Crear, Leer, Actualizar, Eliminar) para las habitaciones.
 */
@Controller
@RequestMapping("/admin/habitaciones")
public class AdminHabitacionController {

    private final ServicioHabitacion servicioHabitacion;
    private final ServicioTipoHabitacion servicioTipoHabitacion;
    private final ServicioHotel servicioHotel;
    private final ServicioImagen servicioImagen;
    private final FileSystemStorageService fileSystemStorageService;


    /**
     * Constructor que inyecta las dependencias necesarias para el controlador.
     *
     * @param servicioHabitacion          Servicio para gestionar habitaciones.
     * @param servicioTipoHabitacion      Servicio para gestionar los tipos de habitaciones.
     * @param servicioHotel               Servicio para gestionar hoteles.
     * @param servicioImagen              Servicio para gestionar imágenes de habitaciones.
     * @param fileSystemStorageService    Servicio para gestionar el almacenamiento de archivos.
     */
    public AdminHabitacionController(ServicioHabitacion servicioHabitacion, ServicioTipoHabitacion servicioTipoHabitacion, ServicioHotel servicioHotel, ServicioImagen servicioImagen, FileSystemStorageService fileSystemStorageService) {
        this.servicioHabitacion = servicioHabitacion;
        this.servicioTipoHabitacion = servicioTipoHabitacion;
        this.servicioHotel = servicioHotel;
        this.servicioImagen = servicioImagen;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    /**
     * Maneja la solicitud GET para listar todas las habitaciones con paginación.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param page   Número de página actual (por defecto es 0).
     * @param size   Tamaño de la página (por defecto es 10).
     * @return El nombre de la plantilla que muestra la lista de habitaciones.
     */
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

    /**
     * Maneja la solicitud GET para mostrar la página de creación de una nueva habitación.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra el formulario de creación de una nueva habitación.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        HabitacionFormDto habitacionFormDto = new HabitacionFormDto();
        modelo.addAttribute("habitacionFormDto", habitacionFormDto);
        modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
        modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
        return "admin/habitaciones/adminNuevaHabitacion";
    }

    /**
     * Maneja la solicitud POST para crear una nueva habitación.
     *
     * @param habitacionFormDto Objeto DTO que contiene los datos del formulario.
     * @param result            Objeto que contiene los errores de validación.
     * @param modelo            Modelo para pasar datos a la vista.
     * @return Redirige a la lista de habitaciones si la creación es exitosa, de lo contrario, devuelve la página de creación con errores.
     */
    @PostMapping("/crear")
    public String crear(
            @Valid @ModelAttribute("habitacionFormDto") HabitacionFormDto habitacionFormDto,
            BindingResult result,
            Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
            return "admin/habitaciones/adminNuevaHabitacion";
        }

        try {
            // Crear la habitación
            Habitacion habitacion = new Habitacion();
            habitacion.setNumero(habitacionFormDto.getNumero());
            habitacion.setCapacidad(habitacionFormDto.getCapacidad());
            habitacion.setNumeroCamas(habitacionFormDto.getNumeroCamas());

            Optional<Hotel> optHotel = servicioHotel.encuentraPorId(habitacionFormDto.getHotelId());
            Optional<TipoHabitacion> optTipo = servicioTipoHabitacion.encuentraPorId(habitacionFormDto.getTipoId());

            optHotel.ifPresent(habitacion::setHotel);
            optTipo.ifPresent(habitacion::setTipo);

            // Guardar la habitación sin imágenes primero
            Habitacion habitacionGuardada = servicioHabitacion.guardar(habitacion);

            // Procesar las imágenes
            if (habitacionFormDto.getImagenes() != null) {
                for (MultipartFile archivo : habitacionFormDto.getImagenes()) {
                    if (!archivo.isEmpty()) {
                        Imagen imagenBD = new Imagen();
                        imagenBD.setHabitacionImagen(habitacionGuardada);
                        String FILE_NAME = "habitacion-" + habitacionGuardada.getId() + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(archivo.getOriginalFilename());
                        imagenBD.setUrl(FILE_NAME);

                        // Almacenar la imagen en el sistema de archivos
                        fileSystemStorageService.store(archivo, FILE_NAME);

                        // Agregar la imagen a la habitación
                        habitacionGuardada.getImagenesHabitacion().add(imagenBD);

                        // Guardar la imagen en la base de datos
                        servicioImagen.guardar(imagenBD);
                    }
                }
            }

            // Guardar la habitación con las nuevas imágenes
            servicioHabitacion.guardar(habitacionGuardada);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/habitaciones";
    }

    /**
     * Maneja la solicitud GET para mostrar la página de edición de una habitación existente.
     *
     * @param id     ID de la habitación a editar.
     * @param modelo Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra el formulario de edición de una habitación.
     */
    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(@PathVariable Integer id, Model modelo) {
        Optional<Habitacion> habitacionOptional = servicioHabitacion.encuentraPorId(id);
        if (habitacionOptional.isPresent()) {
            Habitacion habitacion = habitacionOptional.get();

            HabitacionFormDto habitacionFormDto = HabitacionFormDto.from(habitacion);

            modelo.addAttribute("habitacionFormDto", habitacionFormDto);
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
            modelo.addAttribute("imagenesHabitacion", habitacion.getImagenesHabitacion());

            return "admin/habitaciones/adminDetallesHabitacion";
        } else {
            return "error/paginaError";
        }
    }

    /**
     * Maneja la solicitud PUT para actualizar una habitación existente.
     *
     * @param id                  ID de la habitación a actualizar.
     * @param imagenes            Array de archivos de imágenes a subir.
     * @param habitacionFormDto   Objeto DTO que contiene los datos del formulario.
     * @param result              Objeto que contiene los errores de validación.
     * @param modelo              Modelo para pasar datos a la vista.
     * @return Redirige a la lista de habitaciones si la actualización es exitosa, de lo contrario, devuelve la página de edición con errores.
     */
    @PutMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @RequestParam(name = "imagenes", required = false) MultipartFile[] imagenes,
            @Valid @ModelAttribute("habitacionFormDto") HabitacionFormDto habitacionFormDto,
            BindingResult result,
            Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("hoteles", servicioHotel.buscarEntidades());
            modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
            return "admin/habitaciones/adminDetallesHabitacion";
        }

        try {
            Optional<Habitacion> habitacionOptional = servicioHabitacion.encuentraPorId(id);
            if (habitacionOptional.isPresent()) {
                Habitacion habitacionExistente = habitacionOptional.get();

                habitacionExistente.setNumero(habitacionFormDto.getNumero());
                habitacionExistente.setCapacidad(habitacionFormDto.getCapacidad());
                habitacionExistente.setNumeroCamas(habitacionFormDto.getNumeroCamas());
                habitacionExistente.getTipo().setDescripcion(habitacionFormDto.getDescripcion());

                habitacionExistente.setHotel(servicioHotel.encuentraPorId(habitacionFormDto.getHotelId()).orElse(null));
                habitacionExistente.setTipo(servicioTipoHabitacion.encuentraPorId(habitacionFormDto.getTipoId()).orElse(null));

                if (imagenes != null && imagenes.length > 0) {
                    boolean algunaImagenNueva = false;

                    for (MultipartFile archivo : imagenes) {
                        if (!archivo.isEmpty()) {
                            algunaImagenNueva = true;
                            break;
                        }
                    }

                    if (algunaImagenNueva) {
                        habitacionExistente.getImagenesHabitacion().clear();

                        for (MultipartFile archivo : imagenes) {
                            if (!archivo.isEmpty()) {
                                Imagen imagenBD = new Imagen();
                                imagenBD.setHabitacionImagen(habitacionExistente);

                                String FILE_NAME = "habitacion-" + habitacionExistente.getId() + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(archivo.getOriginalFilename());
                                imagenBD.setUrl(FILE_NAME);

                                fileSystemStorageService.store(archivo, FILE_NAME);

                                habitacionExistente.getImagenesHabitacion().add(imagenBD);
                                servicioImagen.guardar(imagenBD);
                            }
                        }
                    }
                }

                servicioHabitacion.guardar(habitacionExistente);
            } else {
                modelo.addAttribute("error", "Habitación no encontrada");
                return "error/paginaError";
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar la habitación", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/habitaciones";
    }

    /**
     * Maneja la solicitud DELETE para eliminar una habitación existente.
     *
     * @param id     ID de la habitación a eliminar.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirige a la lista de habitaciones si la eliminación es exitosa, de lo contrario, devuelve un mensaje de error.
     */
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
