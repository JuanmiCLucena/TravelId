package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.servicios.*;
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
        modelo.addAttribute("actividades",actividades);
        modelo.addAttribute("page", actividadesPage);
        return "admin/adminActividades";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Actividad> actividad = servicioActividad.encuentraPorId(id);
        if(actividad.isPresent()) {
            modelo.addAttribute("actividad",actividad.get());
            modelo.addAttribute("preciosActuales",
                    servicioActividad.getPrecioActual(actividad.get(), LocalDateTime.now()));
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());

        return "admin/adminDetallesActividad";
        } else {
            // Actividad no encontrado - htlm
            return "actividadNoEncontrado";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Actividad actividad = new Actividad();
        modelo.addAttribute("actividad", actividad);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());
        return "admin/adminNuevaActividad";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam(name = "imagen") MultipartFile imagen, @ModelAttribute("actividad") Actividad actividad) {

        try {

            String FILE_NAME;

            servicioActividad.guardar(actividad);
            Imagen imagenBD = new Imagen();
            imagenBD.setActividad(actividad);
            imagenBD.setUrl(String.valueOf(actividad.getId()));
            servicioImagen.guardar(imagenBD);
            FILE_NAME = "actividad-" + actividad.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
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

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Actividad> optionalActividad = servicioActividad.encuentraPorId(id);
        if(optionalActividad.isPresent()) {
            servicioActividad.eliminarPorId(id);
        }
        return "redirect:/admin/actividades";
    }

}
