package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoUsuario;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.ServicioReserva;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ServicioReserva servicioReserva;
    private final ServicioHabitacion servicioHabitacion;
    private final RepoUsuario repoUsuario;

    public ReservaController(ServicioReserva servicioReserva, ServicioHabitacion servicioHabitacion, RepoUsuario repoUsuario) {
        this.servicioReserva = servicioReserva;
        this.servicioHabitacion = servicioHabitacion;
        this.repoUsuario = repoUsuario;
    }

    @GetMapping("/habitacion/reservar/{id}")
    public String mostrarPaginaCrear(Model modelo, @PathVariable Integer id) {
        Optional<Habitacion> optionalHabitacion = servicioHabitacion.encuentraPorId(id);
        if(optionalHabitacion.isPresent()) {
            Habitacion habitacion = optionalHabitacion.get();
            modelo.addAttribute("habitacion", habitacion);
        }
        return "reservaHabitacion";
    }

    @PostMapping("/crear")
    public String crearReserva(@RequestParam String fechaInicio,
                               @RequestParam String fechaFin,
                               Principal principal,
                               Model model) {
        try {
            // Obtener el usuario autenticado a partir del Principal
            String username = principal.getName();
            Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(username); // Implementa este método para obtener el usuario desde el repositorio

            if(optionalUsuario.isPresent()) {

                Usuario usuario = optionalUsuario.get();

                // Parsear las fechas
                LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
                LocalDateTime fin = LocalDateTime.parse(fechaFin);

                // Crear la reserva
                Reserva reserva = servicioReserva.crearReserva(usuario, inicio, fin);

                // Agregar el ID de la reserva al modelo
                model.addAttribute("reservaId", reserva.getId());
            }
            // Redirigir al usuario a la página para modificar la reserva
            return "redirect:/reservas";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }


}
