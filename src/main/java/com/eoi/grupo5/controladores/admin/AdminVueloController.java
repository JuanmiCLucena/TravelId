package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.dtos.VueloFormDto;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
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

    private final ServicioCompaniaVuelo servicioCompaniaVuelo;
    private final ServicioVuelo servicioVuelo;
    private final ServicioAsiento servicioAsiento;
    private final ServicioImagen servicioImagen;
    private final ServicioLocalizacion servicioLocalizacion;

    private final FileSystemStorageService fileSystemStorageService;


    public AdminVueloController(ServicioCompaniaVuelo servicioCompaniaVuelo, ServicioVuelo servicioVuelo, ServicioAsiento servicioAsiento, ServicioImagen servicioImagen, ServicioLocalizacion servicioLocalizacion, FileSystemStorageService fileSystemStorageService) {
        this.servicioCompaniaVuelo = servicioCompaniaVuelo;
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
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
        PaginaRespuestaVuelos<Vuelo> vuelosPage = servicioVuelo.buscarEntidadesPaginadas(page, size);
        List<Vuelo> vuelos = vuelosPage.getContent();
        modelo.addAttribute("vuelos",vuelos);
        modelo.addAttribute("page", vuelosPage);
        return "admin/vuelos/adminVuelos";
    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Vuelo vuelo = new Vuelo();
        modelo.addAttribute("vuelo", vuelo);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        modelo.addAttribute("companias", servicioCompaniaVuelo.buscarEntidades());
        return "admin/vuelos/adminNuevoVuelo";
    }

    @PostMapping("/crear")
    public String crear(@RequestParam MultipartFile imagen, @ModelAttribute("vuelo") VueloFormDto vueloFormDto) {
        try {

            // Mapeamos el vuelo del formulario a nuestra entidad Vuelo
            Vuelo vuelo = Vuelo.from(vueloFormDto);

            // Guardar el vuelo primero para obtener el ID
            servicioVuelo.guardar(vuelo);

            // Crear y guardar una imagen temporalmente con URL temporal
            Imagen imagenBD = new Imagen();
            imagenBD.setVuelo(vuelo);
            imagenBD.setUrl("");  // Deja la URL en blanco por ahora
            servicioImagen.guardar(imagenBD);

            vuelo.setImagen(imagenBD);

            // Generar el nombre del archivo y guardar la imagen
            String extension = FilenameUtils.getExtension(imagen.getOriginalFilename());
            String fileName = "vuelo-" + vuelo.getId() + "-" + imagenBD.getId() + "." + extension;
            fileSystemStorageService.store(imagen, fileName);

            // Actualizar la URL de la imagen en la base de datos
            imagenBD.setUrl(fileName);
            servicioImagen.guardar(imagenBD);

            // Asociar la imagen con el vuelo y guardar el vuelo nuevamente
            vuelo.setImagen(imagenBD);
            servicioVuelo.guardar(vuelo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/vuelos";
    }

    @GetMapping("editar/{id}")
    public String mostrarPaginaEditar(Model modelo, @PathVariable Integer id) {
        Optional<Vuelo> vuelo = servicioVuelo.encuentraPorId(id);
        if (vuelo.isPresent()) {
            modelo.addAttribute("vuelo", vuelo.get());
            modelo.addAttribute("preciosActuales", servicioAsiento.obtenerPreciosActualesAsientosVuelo(vuelo.get()));
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("companias", servicioCompaniaVuelo.buscarEntidades());
            return "admin/vuelos/adminDetallesVuelo";
        } else {
            modelo.addAttribute("error", "Vuelo no encontrado");
            return "admin/vuelos/adminVuelos";
        }
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
