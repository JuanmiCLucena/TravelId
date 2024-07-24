package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoUsuario;
import com.eoi.grupo5.servicios.ServicioReserva;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ServicioReserva servicioReserva;
    private final RepoUsuario repoUsuario;

    public ReservaController(ServicioReserva servicioReserva, RepoUsuario repoUsuario) {
        this.servicioReserva = servicioReserva;
        this.repoUsuario = repoUsuario;
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
}
