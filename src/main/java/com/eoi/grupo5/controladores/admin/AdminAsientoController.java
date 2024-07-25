package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaAsientos;
import com.eoi.grupo5.paginacion.PaginaRespuestaHabitaciones;
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
@RequestMapping("/admin/asientos")
public class AdminAsientoController {

    private final ServicioAsiento servicioAsiento;
    private final ServicioCategoriaAsiento servicioCategoriaAsiento;
    private final ServicioVuelo servicioVuelo;



    public AdminAsientoController(ServicioHabitacion servicioHabitacion, ServicioTipoHabitacion servicioTipoHabitacion, ServicioHotel servicioHotel, ServicioAsiento servicioAsiento, ServicioCategoriaAsiento servicioCategoriaAsiento, ServicioVuelo servicioVuelo, ServicioImagen servicioImagen, FileSystemStorageService fileSystemStorageService) {
        this.servicioAsiento = servicioAsiento;
        this.servicioCategoriaAsiento = servicioCategoriaAsiento;
        this.servicioVuelo = servicioVuelo;

    }

    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaAsientos<Asiento> asientosPage = servicioAsiento.buscarEntidadesPaginadas(page, size);
        List<Asiento> asientos = asientosPage.getContent();
        modelo.addAttribute("asientos",asientos);
        modelo.addAttribute("page", asientosPage);
        return "admin/adminAsientos";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Asiento> asiento = servicioAsiento.encuentraPorId(id);
        if(asiento.isPresent()) {
            modelo.addAttribute("asiento",asiento.get());
            modelo.addAttribute("precioActual",
                    servicioAsiento.getPrecioActual(asiento.get(), LocalDateTime.now()));
            modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
            modelo.addAttribute("categoriasAsiento", servicioCategoriaAsiento.buscarEntidades());

        return "admin/adminDetallesAsiento";
        } else {
            // Habitacion no encontrado - htlm
            return "asientoNoEncontrado";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Asiento asiento = new Asiento();
        modelo.addAttribute("asiento", asiento);
        modelo.addAttribute("vuelos", servicioVuelo.buscarEntidades());
        modelo.addAttribute("categoriasAsiento", servicioCategoriaAsiento.buscarEntidades());
        return "admin/adminNuevoAsiento";
    }

    @PostMapping("/crear")
    public String crear(
            @ModelAttribute("asiento") Asiento asiento,
            @RequestParam("categoria.id") Integer categoriaId,
            @RequestParam("vuelo.id") Integer vueloId
            ) {

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

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Asiento> optionalAsiento = servicioAsiento.encuentraPorId(id);
        if(optionalAsiento.isPresent()) {
            servicioAsiento.eliminarPorId(id);
        }
        return "redirect:/admin/asientos";
    }

}
