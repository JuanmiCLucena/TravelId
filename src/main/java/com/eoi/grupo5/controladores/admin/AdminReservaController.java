package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
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
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    private final ServicioReserva servicioReserva;
    private final ServicioUsuario servicioUsuario;

    public AdminReservaController(ServicioReserva servicioReserva, ServicioUsuario servicioUsuario) {
        this.servicioReserva = servicioReserva;
        this.servicioUsuario = servicioUsuario;
    }

    @GetMapping
    public String listar(Model modelo) {
        List<Reserva> reservas = servicioReserva.buscarEntidades();
        modelo.addAttribute("reservas",reservas);
        return "admin/adminReservas";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Reserva> reserva = servicioReserva.encuentraPorId(id);
        if(reserva.isPresent()) {
            modelo.addAttribute("reserva",reserva.get());
            modelo.addAttribute("usuarios", servicioUsuario.buscarEntidades());

            return "admin/adminDetallesReserva";
        } else {
            // Habitacion no encontrado - htlm
            return "reservaNoEncontrada";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Reserva reserva = new Reserva();
        modelo.addAttribute("reserva", reserva);
        modelo.addAttribute("usuarios", servicioUsuario.buscarEntidades());
        return "admin/adminNuevaReserva";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("reserva") Reserva reserva, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("usuarios", servicioUsuario.buscarEntidades());
            return "admin/adminNuevaReserva";
        }
        try {
            servicioReserva.guardar(reserva);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/admin/reservas";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Reserva> optionalReserva = servicioReserva.encuentraPorId(id);
        if(optionalReserva.isPresent()) {
            servicioReserva.eliminarPorId(id);
        }
        return "redirect:/admin/reservas";
    }

}
