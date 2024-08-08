package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.servicios.*;
import com.eoi.grupo5.servicios.archivos.FileSystemStorageService;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/actividades")
public class AdminActividadController {

    private final ServicioActividad servicioActividad;
    private final ServicioTipoActividad servicioTipoActividad;
    private final ServicioImagen servicioImagen;
    private final ServicioLocalizacion servicioLocalizacion;
    private final FileSystemStorageService fileSystemStorageService;

    public AdminActividadController(ServicioActividad servicioActividad, ServicioTipoActividad servicioTipoActividad, ServicioImagen servicioImagen, ServicioLocalizacion servicioLocalizacion, FileSystemStorageService fileSystemStorageService) {
        this.servicioActividad = servicioActividad;
        this.servicioTipoActividad = servicioTipoActividad;
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
        PaginaRespuestaActividades<Actividad> actividadesPage = servicioActividad.buscarEntidadesPaginadas(page, size);
        List<Actividad> actividades = actividadesPage.getContent();
        modelo.addAttribute("actividades", actividades);
        modelo.addAttribute("page", actividadesPage);
        return "admin/actividades/adminActividades";
    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Actividad actividad = new Actividad();
        modelo.addAttribute("actividad", actividad);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());
        return "admin/actividades/adminNuevaActividad";
    }

    @PostMapping("/crear")
    public String crear(
            @RequestParam(name = "imagen") MultipartFile imagen,
            @Valid @ModelAttribute("actividad") Actividad actividad,
            BindingResult result,
            Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());
            return "admin/actividades/adminNuevaActividad";
        }

        try {
            servicioActividad.guardar(actividad);
            Imagen imagenBD = new Imagen();
            imagenBD.setActividad(actividad);
            imagenBD.setUrl(String.valueOf(actividad.getId()));
            servicioImagen.guardar(imagenBD);
            String FILE_NAME = "actividad-" + actividad.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
            imagenBD.setUrl(FILE_NAME);
            actividad.getImagenes().clear();
            fileSystemStorageService.store(imagen, FILE_NAME);
            actividad.getImagenes().add(imagenBD);
            servicioActividad.guardar(actividad);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/admin/actividades";
    }

    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(@PathVariable Integer id, Model modelo) {
        Optional<Actividad> actividad = servicioActividad.encuentraPorId(id);
        if (actividad.isPresent()) {
            modelo.addAttribute("actividad", actividad.get());
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());
            return "admin/actividades/adminDetallesActividad";
        } else {
            // Actividad no encontrado - html
            return "error/paginaError";
        }
    }

    @PutMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @RequestParam(name = "imagen", required = false) MultipartFile imagen,
            @Valid @ModelAttribute("actividad") Actividad actividad,
            BindingResult result,
            Model modelo) {

        // Verifica si hay errores en los datos del formulario
        if (result.hasErrors()) {
            // Agregar las listas necesarias al modelo para el formulario
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());

            // Devuelve la vista con los errores del formulario
            return "admin/actividades/adminDetallesActividad";
        }

        try {
            Optional<Actividad> actividadOptional = servicioActividad.encuentraPorId(id);
            if (actividadOptional.isPresent()) {
                Actividad actividadExistente = actividadOptional.get();
                // Actualiza los campos de la actividad existente con los nuevos valores
                actividadExistente.setNombre(actividad.getNombre());
                actividadExistente.setDescripcion(actividad.getDescripcion());
                actividadExistente.setFechaInicio(actividad.getFechaInicio());
                actividadExistente.setFechaFin(actividad.getFechaFin());
                actividadExistente.setLocalizacion(actividad.getLocalizacion());
                actividadExistente.setTipo(actividad.getTipo());
                actividadExistente.setMaximosAsistentes(actividad.getMaximosAsistentes());
                actividadExistente.setAsistentesConfirmados(actividad.getAsistentesConfirmados());

                // Maneja la imagen si se ha subido una nueva
                if (imagen != null && !imagen.isEmpty()) {
                    Imagen imagenBD = new Imagen();
                    imagenBD.setActividad(actividadExistente);
                    // Generar un nombre único para la imagen
                    String FILE_NAME = "actividad-" + actividadExistente.getId() + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
                    imagenBD.setUrl(FILE_NAME);

                    // Guardar la imagen en el sistema de archivos
                    fileSystemStorageService.store(imagen, FILE_NAME);

                    // Limpiar imágenes existentes y añadir la nueva imagen
                    actividadExistente.getImagenes().clear();
                    actividadExistente.getImagenes().add(imagenBD);

                    // Guardar la imagen en la base de datos
                    servicioImagen.guardar(imagenBD);
                }

                // Guardar la actividad actualizada
                servicioActividad.guardar(actividadExistente);
            }
        } catch (Exception e) {
            // Manejo de excepciones
            throw new RuntimeException("Error al actualizar la actividad", e);
        }

        // Redirige a la lista de actividades después de guardar los cambios
        return "redirect:/admin/actividades";
    }


    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Actividad> optionalActividad = servicioActividad.encuentraPorId(id);
        if (optionalActividad.isPresent()) {
            servicioActividad.eliminarPorId(id);
        }
        return "redirect:/admin/actividades";
    }
}
