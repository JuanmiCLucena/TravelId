package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaAsientos;
import com.eoi.grupo5.servicios.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la administración de asientos en el sistema.
 * Proporciona las operaciones CRUD para gestionar los asientos, vinculándolos a vuelos y categorías.
 *
 * <p>Este controlador maneja rutas bajo el contexto "/admin/asientos".</p>
 */
@Controller
@RequestMapping("/admin/asientos")
public class AdminAsientoController {

    private final ServicioAsiento servicioAsiento;
    private final ServicioCategoriaAsiento servicioCategoriaAsiento;
    private final ServicioVuelo servicioVuelo;

    /**
     * Constructor para inicializar los servicios requeridos por el controlador.
     *
     * @param servicioAsiento        Servicio para la gestión de asientos.
     * @param servicioCategoriaAsiento Servicio para la gestión de categorías de asientos.
     * @param servicioVuelo          Servicio para la gestión de vuelos.
     */
    public AdminAsientoController(ServicioAsiento servicioAsiento, ServicioCategoriaAsiento servicioCategoriaAsiento, ServicioVuelo servicioVuelo) {
        this.servicioAsiento = servicioAsiento;
        this.servicioCategoriaAsiento = servicioCategoriaAsiento;
        this.servicioVuelo = servicioVuelo;
    }

    /**
     * Muestra la lista de asientos en una página de administración con paginación.
     *
     * @param modelo Objeto Model para agregar atributos a la vista.
     * @param page   Número de página para la paginación (por defecto 0).
     * @param size   Tamaño de la página para la paginación (por defecto 10).
     * @return El nombre de la vista que muestra la lista de asientos.
     */
    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaAsientos<Asiento> asientosPage = servicioAsiento.buscarEntidadesPaginadas(page, size);
        List<Asiento> asientos = asientosPage.getContent();
        modelo.addAttribute("asientos", asientos);
        modelo.addAttribute("page", asientosPage);
        return "admin/asientos/adminAsientos";
    }

    /**
     * Muestra el formulario para crear un nuevo asiento.
     *
     * @param modelo Objeto Model para agregar atributos a la vista.
     * @return El nombre de la vista que contiene el formulario de creación de asiento.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Asiento asiento = new Asiento();
        modelo.addAttribute("asiento", asiento);
        modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
        modelo.addAttribute("categoriasAsiento", servicioCategoriaAsiento.buscarEntidades());
        return "admin/asientos/adminNuevoAsiento";
    }

    /**
     * Procesa la creación de un nuevo asiento.
     *
     * @param asiento       Objeto Asiento que contiene los datos del formulario.
     * @param bindingResult Objeto BindingResult para manejar errores de validación.
     * @param categoriaId   ID de la categoría seleccionada para el asiento.
     * @param vueloId       ID del vuelo asociado al asiento.
     * @param modelo        Objeto Model para agregar atributos a la vista.
     * @return Redirección a la lista de asientos si la creación es exitosa, o la vista de creación si hay errores.
     */
    @PostMapping("/crear")
    public String crear(
            @ModelAttribute("asiento") @Valid Asiento asiento,
            BindingResult bindingResult,
            @RequestParam("categoria.id") Integer categoriaId,
            @RequestParam("vuelo.id") Integer vueloId,
            Model modelo
    ) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("categoriasAsiento", servicioCategoriaAsiento.buscarEntidades());
            return "admin/asientos/adminNuevoAsiento";
        }

        try {
            CategoriaAsiento categoria = servicioCategoriaAsiento.encuentraPorId(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            asiento.setCategoria(categoria);

            Vuelo vuelo = servicioVuelo.encuentraPorId(vueloId)
                    .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
            asiento.setVuelo(vuelo);

            servicioAsiento.guardar(asiento);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/asientos";
    }

    /**
     * Muestra el formulario para editar un asiento existente.
     *
     * @param id     ID del asiento a editar.
     * @param modelo Objeto Model para agregar atributos a la vista.
     * @return El nombre de la vista que contiene el formulario de edición, o una página de error si el asiento no se encuentra.
     */
    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(@PathVariable Integer id, Model modelo) {
        Optional<Asiento> asiento = servicioAsiento.encuentraPorId(id);
        if (asiento.isPresent()) {
            modelo.addAttribute("asiento", asiento.get());
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("categoriasAsiento", servicioCategoriaAsiento.buscarEntidades());
            return "admin/asientos/adminDetallesAsiento";
        } else {
            return "asientoNoEncontrado";
        }
    }

    /**
     * Procesa la edición de un asiento existente.
     *
     * @param id            ID del asiento a editar.
     * @param asiento       Objeto Asiento que contiene los datos del formulario.
     * @param bindingResult Objeto BindingResult para manejar errores de validación.
     * @param categoriaId   ID de la categoría seleccionada para el asiento.
     * @param vueloId       ID del vuelo asociado al asiento.
     * @param modelo        Objeto Model para agregar atributos a la vista.
     * @return Redirección a la lista de asientos si la edición es exitosa, o la vista de edición si hay errores.
     */
    @PutMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @ModelAttribute("asiento") @Valid Asiento asiento,
            BindingResult bindingResult,
            @RequestParam("categoria.id") Integer categoriaId,
            @RequestParam("vuelo.id") Integer vueloId,
            Model modelo
    ) {
        // Verificar si el asiento existe
        Optional<Asiento> asientoExistente = servicioAsiento.encuentraPorId(id);
        if (asientoExistente.isEmpty()) {
            // Asiento no encontrado, redirigir a una página de error o no encontrado
            return "redirect:/error/paginaError";
        }

        // Si hay errores de validación
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("categoriasAsiento", servicioCategoriaAsiento.buscarEntidades());
            return "admin/asientos/adminDetallesAsiento";
        }

        try {
            // Actualizar el asiento con los datos del formulario
            CategoriaAsiento categoria = servicioCategoriaAsiento.encuentraPorId(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            asiento.setCategoria(categoria);

            Vuelo vuelo = servicioVuelo.encuentraPorId(vueloId)
                    .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
            asiento.setVuelo(vuelo);

            // Guardar los cambios
            servicioAsiento.guardar(asiento);
        } catch (Exception e) {
            // Manejar la excepción adecuadamente, por ejemplo, registrar el error
            throw new RuntimeException(e);
        }

        // Redirigir al listado de asientos después de la edición exitosa
        return "redirect:/admin/asientos";
    }

    /**
     * Elimina un asiento existente.
     *
     * @param id ID del asiento a eliminar.
     * @return Redirección a la lista de asientos tras la eliminación.
     */
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Asiento> optionalAsiento = servicioAsiento.encuentraPorId(id);
        if (optionalAsiento.isPresent()) {
            servicioAsiento.eliminarPorId(id);
        }
        return "redirect:/admin/asientos";
    }
}
