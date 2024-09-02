package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaReservas;
import com.eoi.grupo5.repos.RepoReservaActividad;
import com.eoi.grupo5.servicios.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar las operaciones de administración relacionadas con las reservas.
 * Permite crear, listar, editar y eliminar reservas desde la interfaz de administración.
 */
@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ServicioReserva servicioReserva;
    private final RepoReservaActividad repoReservaActividad;
    private final ServicioUsuario servicioUsuario;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param servicioReserva Servicio para manejar las operaciones de reserva.
     * @param repoReservaActividad Repositorio para manejar las reservas de actividades.
     * @param servicioUsuario Servicio para manejar las operaciones de usuario.
     */
    public AdminReservaController(ServicioReserva servicioReserva, RepoReservaActividad repoReservaActividad, ServicioUsuario servicioUsuario) {
        this.servicioReserva = servicioReserva;
        this.repoReservaActividad = repoReservaActividad;
        this.servicioUsuario = servicioUsuario;
    }

    /**
     * Maneja la solicitud GET para listar todas las reservas paginadas.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param page Número de la página solicitada (por defecto 0).
     * @param size Tamaño de la página (por defecto 10).
     * @return El nombre de la plantilla que muestra la lista de reservas.
     */
    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaReservas<Reserva> reservasPage = servicioReserva.buscarEntidadesPaginadas(page, size);
        List<Reserva> reservas = servicioReserva.buscarEntidades();
        modelo.addAttribute("reservas", reservas);
        modelo.addAttribute("page", reservasPage);
        return "admin/reservas/adminReservas";
    }

    /**
     * Muestra la página para crear una nueva reserva.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra el formulario de creación de reserva.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Reserva reserva = new Reserva();
        modelo.addAttribute("reserva", reserva);
        modelo.addAttribute("usuarios", servicioUsuario.buscarEntidades());
        return "admin/reservas/adminNuevaReserva";
    }

    /**
     * Maneja la solicitud POST para crear una nueva reserva.
     *
     * @param reserva La reserva que se va a crear, validada.
     * @param result Resultado de la validación de la reserva.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de reservas o la vista de creación si hay errores.
     */
    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("reserva") Reserva reserva, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("usuarios", servicioUsuario.buscarEntidades());
            return "admin/reservas/adminNuevaReserva";
        }
        try {
            servicioReserva.guardar(reserva);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/admin/reservas";
    }

    /**
     * Muestra la página para editar una reserva existente.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param id ID de la reserva que se va a editar.
     * @return El nombre de la plantilla que muestra el formulario de edición de reserva, o una página de error si no se encuentra la reserva.
     */
    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(Model modelo, @PathVariable Integer id) {
        Optional<Reserva> reserva = servicioReserva.encuentraPorId(id);
        if (reserva.isPresent()) {
            modelo.addAttribute("reserva", reserva.get());
            modelo.addAttribute("usuarios", servicioUsuario.buscarEntidades());
            return "admin/reservas/adminDetallesReserva";
        } else {
            // Reserva no encontrada - html
            return "error/paginaError";
        }
    }

    /**
     * Maneja la solicitud PUT para editar una reserva existente.
     *
     * @param id ID de la reserva que se va a actualizar.
     * @param reserva La reserva actualizada, validada.
     * @param result Resultado de la validación de la reserva.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de reservas o la vista de edición si hay errores.
     */
    @PutMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, @Valid @ModelAttribute("reserva") Reserva reserva, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("usuarios", servicioUsuario.buscarEntidades());
            return "admin/reservas/adminDetallesReserva";
        }
        Optional<Reserva> optionalReserva = servicioReserva.encuentraPorId(id);
        if (optionalReserva.isPresent()) {
            reserva.setId(id);
            try {
                servicioReserva.guardar(reserva);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return "redirect:/admin/reservas";
        } else {
            return "reservaNoEncontrada";
        }
    }

    /**
     * Maneja la solicitud DELETE para eliminar una reserva existente.
     *
     * @param id ID de la reserva que se va a eliminar.
     * @return Redirección a la lista de reservas.
     */
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Reserva> optionalReserva = servicioReserva.encuentraPorId(id);
        if (optionalReserva.isPresent()) {
            servicioReserva.eliminarPorId(id);
        }
        return "redirect:/admin/reservas";
    }
}
