package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaReservas;
import com.eoi.grupo5.servicios.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ServicioReserva servicioReserva;
    private final ServicioUsuario servicioUsuario;

    public AdminReservaController(ServicioReserva servicioReserva, ServicioUsuario servicioUsuario) {
        this.servicioReserva = servicioReserva;
        this.servicioUsuario = servicioUsuario;
    }

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

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Reserva reserva = new Reserva();
        modelo.addAttribute("reserva", reserva);
        modelo.addAttribute("usuarios", servicioUsuario.buscarEntidades());
        return "admin/reservas/adminNuevaReserva";
    }

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

    @PostMapping("/editar/{id}")
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

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Reserva> optionalReserva = servicioReserva.encuentraPorId(id);
        if (optionalReserva.isPresent()) {
            servicioReserva.eliminarPorId(id);
        }
        return "redirect:/admin/reservas";
    }
}
