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

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Actividad> actividad = servicioActividad.encuentraPorId(id);
        if (actividad.isPresent()) {
            modelo.addAttribute("actividad", actividad.get());
            modelo.addAttribute("preciosActuales", servicioActividad.getPrecioActual(actividad.get(), LocalDateTime.now()));
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());
            return "admin/actividades/adminDetallesActividad";
        } else {
            // Actividad no encontrado - html
            return "error/paginaError";
        }
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

    @PostMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @RequestParam(name = "imagen", required = false) MultipartFile imagen,
            @Valid @ModelAttribute("actividad") Actividad actividad,
            BindingResult result,
            Model modelo) {


        if (result.hasErrors()) {
            // Agregar las listas necesarias al modelo
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());

            // Devolver la vista con errores
            return "admin/actividades/adminDetallesActividad";
        }


        try {
            Optional<Actividad> actividadOptional = servicioActividad.encuentraPorId(id);
            if (actividadOptional.isPresent()) {
                Actividad actividadExistente = actividadOptional.get();
                actividadExistente.setNombre(actividad.getNombre());
                actividadExistente.setDescripcion(actividad.getDescripcion());

                if (imagen != null && !imagen.isEmpty()) {
                    Imagen imagenBD = new Imagen();
                    imagenBD.setActividad(actividadExistente);
                    imagenBD.setUrl(String.valueOf(actividadExistente.getId()));
                    servicioImagen.guardar(imagenBD);
                    String FILE_NAME = "actividad-" + actividadExistente.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
                    imagenBD.setUrl(FILE_NAME);
                    actividadExistente.getImagenes().clear();
                    fileSystemStorageService.store(imagen, FILE_NAME);
                    actividadExistente.getImagenes().add(imagenBD);
                }
                servicioActividad.guardar(actividadExistente);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
