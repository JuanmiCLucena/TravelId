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
import java.util.List;
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
        if (optionalHabitacion.isPresent()) {
            Habitacion habitacion = optionalHabitacion.get();
            modelo.addAttribute("habitacion", habitacion);
        } else {
            modelo.addAttribute("error", "La habitación no se encuentra.");
            return "error"; // O la vista que maneja errores
        }
        return "reservaHabitacion";
    }


    @GetMapping("/{idHabitacion}/disponibilidad")
    public String obtenerRangosDisponibles(
            @PathVariable Integer idHabitacion,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            Model modelo) {

        Optional<Habitacion> optionalHabitacion = servicioHabitacion.encuentraPorId(idHabitacion);

        Habitacion habitacion = null;
        if (optionalHabitacion.isPresent()) {
            habitacion = optionalHabitacion.get();
        }

        List<ServicioHabitacion.Interval> rangosDisponibles = servicioHabitacion.obtenerRangosDisponibles(idHabitacion, fechaInicio, fechaFin);
        modelo.addAttribute("rangosDisponibles", rangosDisponibles);
        modelo.addAttribute("habitacion", habitacion);
        modelo.addAttribute("fechaInicio", fechaInicio);
        modelo.addAttribute("fechaFin", fechaFin);

        return "disponibilidadHabitacion";
    }



}
