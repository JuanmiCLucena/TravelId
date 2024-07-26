package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoUsuario;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.ServicioReserva;
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

    @PostMapping("/habitacion/reservar")
    public String crearReservaHabitacion(@RequestParam Integer idHabitacion,
                                         @RequestParam LocalDateTime fechaInicio,
                                         @RequestParam LocalDateTime fechaFin,
                                         Principal principal) {
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(principal.getName());
        if (optionalUsuario.isPresent()){
            Usuario usuario = optionalUsuario.get();
            Reserva reserva = servicioReserva.crearReserva(usuario, fechaInicio,fechaFin);
            servicioReserva.addHabitacionToReserva(reserva, idHabitacion, fechaInicio, fechaFin);
        }
        return "redirect:/reservas";
    }

    @PostMapping("/asiento/reservar")
    public String crearReservaAsiento(@RequestParam Integer idAsiento,
                                      @RequestParam LocalDateTime fehaVuelo,
                                      @RequestParam LocalDateTime horaVuelo,
                                      Principal principal){
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(principal.getName());
        if (optionalUsuario.isPresent()){
            Usuario usuario = optionalUsuario.get();
            Reserva reserva = servicioReserva.crearReserva(usuario, fehaVuelo, horaVuelo);
            servicioReserva.addAsientoToReserva(reserva, idAsiento, fehaVuelo, horaVuelo);
        }
        return "redirect:/reservas";
    }

    @PostMapping("/actividad/reservar")
    public String crearReservaActividad(@RequestParam Integer idActividad,
                                      @RequestParam LocalDateTime fechaInicio,
                                      @RequestParam LocalDateTime fechaFin,
                                      Principal principal){
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(principal.getName());
        if (optionalUsuario.isPresent()){
            Usuario usuario = optionalUsuario.get();
            Reserva reserva = servicioReserva.crearReserva(usuario, fechaInicio, fechaFin);
            servicioReserva.reservarActividad(reserva, idActividad, fechaFin, fechaFin);
        }
        return "redirect:/reservas";
    }
}
