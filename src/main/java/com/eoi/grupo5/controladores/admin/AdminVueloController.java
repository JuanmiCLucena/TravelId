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

/**
 * Controlador para gestionar las operaciones de administración relacionadas con los vuelos.
 * Permite listar, crear, editar y eliminar vuelos desde la interfaz de administración.
 */
@Controller
@RequestMapping("/admin/vuelos")
public class AdminVueloController {

    private final ServicioCompaniaVuelo servicioCompaniaVuelo;
    private final ServicioVuelo servicioVuelo;
    private final ServicioAsiento servicioAsiento;
    private final ServicioImagen servicioImagen;
    private final ServicioLocalizacion servicioLocalizacion;

    private final FileSystemStorageService fileSystemStorageService;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param servicioCompaniaVuelo Servicio para manejar las compañías de vuelo.
     * @param servicioVuelo Servicio para manejar las operaciones de vuelo.
     * @param servicioAsiento Servicio para manejar los asientos.
     * @param servicioImagen Servicio para manejar las imágenes.
     * @param servicioLocalizacion Servicio para manejar las localizaciones.
     * @param fileSystemStorageService Servicio para manejar el almacenamiento de archivos.
     */
    public AdminVueloController(ServicioCompaniaVuelo servicioCompaniaVuelo, ServicioVuelo servicioVuelo, ServicioAsiento servicioAsiento, ServicioImagen servicioImagen, ServicioLocalizacion servicioLocalizacion, FileSystemStorageService fileSystemStorageService) {
        this.servicioCompaniaVuelo = servicioCompaniaVuelo;
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
        this.servicioImagen = servicioImagen;
        this.servicioLocalizacion = servicioLocalizacion;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    /**
     * Maneja la solicitud GET para listar todos los vuelos paginados.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param page Número de la página solicitada (por defecto 0).
     * @param size Tamaño de la página (por defecto 10).
     * @return El nombre de la plantilla que muestra la lista de vuelos.
     */
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

    /**
     * Muestra la página para crear un nuevo vuelo.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra el formulario de creación de vuelo.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Vuelo vuelo = new Vuelo();
        modelo.addAttribute("vuelo", vuelo);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        modelo.addAttribute("companias", servicioCompaniaVuelo.buscarEntidades());
        return "admin/vuelos/adminNuevoVuelo";
    }

    /**
     * Maneja la solicitud POST para crear un nuevo vuelo.
     *
     * @param imagen Imagen asociada al vuelo.
     * @param vueloFormDto Datos del nuevo vuelo.
     * @return Redirección a la lista de vuelos.
     */
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

    /**
     * Muestra la página para editar un vuelo existente.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param id ID del vuelo que se va a editar.
     * @return El nombre de la plantilla que muestra el formulario de edición de vuelo, o una vista de error si el vuelo no se encuentra.
     */
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

    /**
     * Maneja la solicitud DELETE para eliminar un vuelo existente.
     *
     * @param id ID del vuelo que se va a eliminar.
     * @return Redirección a la lista de vuelos.
     */
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Vuelo> optionalVuelo = servicioVuelo.encuentraPorId(id);
        if(optionalVuelo.isPresent()) {
            servicioVuelo.eliminarPorId(id);
        }
        return "redirect:/admin/vuelos";
    }

}
