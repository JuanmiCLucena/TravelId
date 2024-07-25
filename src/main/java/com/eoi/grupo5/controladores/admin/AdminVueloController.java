package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.servicios.*;
import com.eoi.grupo5.servicios.archivos.FileSystemStorageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/vuelos")
public class AdminVueloController {

    private final ServicioVuelo servicioVuelo;
    private final ServicioAsiento servicioAsiento;
    private final ServicioImagen servicioImagen;
    private final ServicioLocalizacion servicioLocalizacion;

    private final FileSystemStorageService fileSystemStorageService;


    public AdminVueloController(ServicioVuelo servicioVuelo, ServicioAsiento servicioAsiento, ServicioImagen servicioImagen, ServicioLocalizacion servicioLocalizacion, FileSystemStorageService fileSystemStorageService) {
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
        this.servicioImagen = servicioImagen;
        this.servicioLocalizacion = servicioLocalizacion;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @GetMapping
    public String listar(Model modelo) {
        List<Vuelo> vuelos = servicioVuelo.buscarEntidades();
        modelo.addAttribute("vuelos",vuelos);
        return "admin/adminVuelos";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Vuelo> vuelo = servicioVuelo.encuentraPorId(id);
        if(vuelo.isPresent()) {
            modelo.addAttribute("vuelo",vuelo.get());
            modelo.addAttribute("preciosActuales",
                    servicioAsiento.obtenerPreciosActualesAsientosVuelo(vuelo.get()));
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());

            return "admin/adminDetallesVuelo";
        } else {
            // Hotel no encontrado - htlm
            return "vueloNoEncontrado";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Vuelo vuelo = new Vuelo();
        modelo.addAttribute("vuelo", vuelo);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        return "admin/adminNuevoVuelo";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam(name = "imagen") MultipartFile imagen, @ModelAttribute("vuelo") Vuelo vuelo) {

        try {

            String FILE_NAME;

            servicioVuelo.guardar(vuelo);
            Imagen imagenBD = new Imagen();
            imagenBD.setVuelo(vuelo);
            imagenBD.setUrl(String.valueOf(vuelo.getId()));
            servicioImagen.guardar(imagenBD);
            FILE_NAME = "vuelo-" + vuelo.getId() + "-" + imagenBD.getId() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
            imagenBD.setUrl(FILE_NAME);
            fileSystemStorageService.store(imagen, FILE_NAME);
            vuelo.setImagen(imagenBD);
            servicioVuelo.guardar(vuelo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/vuelos";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Vuelo> optionalVuelo = servicioVuelo.encuentraPorId(id);
        if(optionalVuelo.isPresent()) {
            servicioVuelo.eliminarPorId(id);
        }
        return "redirect:/admin/vuelos";
    }

}
