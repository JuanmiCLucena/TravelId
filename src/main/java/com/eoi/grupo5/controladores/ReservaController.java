package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.repos.RepoUsuario;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.ServicioMetodoPago;
import com.eoi.grupo5.servicios.ServicioReserva;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ServicioReserva servicioReserva;
    private final ServicioHabitacion servicioHabitacion;
    private final RepoUsuario repoUsuario;
    private final ServicioMetodoPago servicioMetodoPago;

    public ReservaController(ServicioReserva servicioReserva, ServicioHabitacion servicioHabitacion, RepoUsuario repoUsuario, ServicioMetodoPago servicioMetodoPago) {
        this.servicioReserva = servicioReserva;
        this.servicioHabitacion = servicioHabitacion;
        this.repoUsuario = repoUsuario;
        this.servicioMetodoPago = servicioMetodoPago;
    }

    @GetMapping("/tus-reservas")
    public String verTusReservas(Principal principal, Model model) {
        // Obtener el usuario autenticado
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(principal.getName());
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Obtener las reservas del usuario
            List<Reserva> reservas = servicioReserva.obtenerReservasPorUsuario(usuario);
            model.addAttribute("reservas", reservas);

            return "reservas/tusReservas";
        } else {
            model.addAttribute("error", "Usuario no encontrado");
            return "error/paginaError";
        }
    }

    @GetMapping("/habitacion/reservar/{id}")
    public String mostrarPaginaCrear(Model modelo, @PathVariable Integer id) {
        Optional<Habitacion> optionalHabitacion = servicioHabitacion.encuentraPorId(id);
        if (optionalHabitacion.isPresent()) {
            Habitacion habitacion = optionalHabitacion.get();
            modelo.addAttribute("habitacion", habitacion);

            if(!habitacion.getPrecio().isEmpty()) {
                Precio precioActual = servicioHabitacion.getPrecioActual(habitacion, LocalDateTime.now());
                modelo.addAttribute("precioActual", precioActual.getValor());
            }

            if(!habitacion.getImagenesHabitacion().isEmpty()) {
                String habitacionImagen = habitacion.getImagenesHabitacion().stream().findFirst().get().getUrl();
                modelo.addAttribute("imagenHabitacion", habitacionImagen);
            }

        } else {
            modelo.addAttribute("error", "La habitación no se encuentra.");
            return "error/paginaError"; // O la vista que maneja errores
        }
        return "reservas/habitacion/disponibilidadHabitacion";
    }

    @PostMapping("/crear")
    public String crearReserva(@RequestParam String fechaInicio,
                               @RequestParam String fechaFin,
                               Principal principal,
                               Model model) {
        try {
            // Obtener el usuario autenticado a partir del Principal
            String username = principal.getName();
            Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(username);

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

    @GetMapping("/habitacion/{idHabitacion}/disponibilidad")
    public String obtenerRangosDisponibles(
            @PathVariable Integer idHabitacion,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            Model modelo,
            RedirectAttributes redirectAttributes) {

        // Truncamos la fecha para que tenga formato HH:mm y así nos aseguramos que no haya problemas
        // en la validación siguiente
        LocalDateTime fechaActual = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        // Validar que las fechas no sean anteriores a la fecha actual
        if (fechaInicio.isBefore(fechaActual) || fechaFin.isBefore(fechaActual)) {
            redirectAttributes.addFlashAttribute("error", "Las fechas no pueden ser anteriores a la fecha actual.");
            return "redirect:/reservas/habitacion/reservar/" + idHabitacion;
        }

        // Validar que la fecha de inicio no sea posterior a la fecha de fin
        if (fechaInicio.isAfter(fechaFin)) {
            redirectAttributes.addFlashAttribute("error", "La fecha de entrada no puede ser posterior a la fecha de llegada.");
            return "redirect:/reservas/habitacion/reservar/" + idHabitacion;
        }

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

        List<MetodoPago> metodosPago = servicioMetodoPago.buscarEntidades();
        modelo.addAttribute("metodosPago", metodosPago);

        return "reservas/habitacion/reservarHabitacion";
    }

    @PostMapping("/habitacion/{idHabitacion}/reservar")
    public String reservarHabitacion(
            @PathVariable("idHabitacion") Integer idHabitacion,
            @RequestParam("fechaInicio") LocalDateTime fechaInicio,
            @RequestParam("fechaFin") LocalDateTime fechaFin,
            @RequestParam("precioTotal") Double precioTotal,
            @RequestParam("metodoPagoId") Integer metodoPagoId,
            Principal principal,
            Model modelo) {

        // Obtener el usuario autenticado
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(principal.getName());
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            // Crear la reserva
            try {
                Reserva reserva = servicioReserva.crearReserva(usuario, fechaInicio, fechaFin);
                servicioReserva.addHabitacion(reserva, idHabitacion, fechaInicio, fechaFin);

                servicioReserva.generarPago(reserva, precioTotal, metodoPagoId);

                return "redirect:/reservas/tus-reservas";
            } catch (Exception e) {
                modelo.addAttribute("error", e.getMessage());
                return "error/paginaError";
            }
        } else {
            modelo.addAttribute("error", "Usuario no encontrado");
            return "error/paginaError";
        }
    }

    @PostMapping("/actividad/{idActividad}/reservar")
    public String reservarActividad(
            @PathVariable("idActividad") Integer idActividad,
            @RequestParam("fechaInicio") LocalDateTime fechaInicio,
            @RequestParam("fechaFin") LocalDateTime fechaFin,
            @RequestParam("precioTotal") Double precioTotal,
            @RequestParam("metodoPagoId") Integer metodoPagoId,
            Principal principal,
            Model modelo) {

        // Obtener el usuario autenticado
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(principal.getName());
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            // Crear la reserva
            try {
                Reserva reserva = servicioReserva.crearReserva(usuario, fechaInicio, fechaFin);
                servicioReserva.addActividad(reserva, idActividad, fechaInicio, fechaFin);

                servicioReserva.generarPago(reserva, precioTotal, metodoPagoId);

                return "redirect:/reservas/tus-reservas";
            } catch (Exception e) {
                modelo.addAttribute("error", e.getMessage());
                return "error/paginaError";
            }
        } else {
            modelo.addAttribute("error", "Usuario no encontrado");
            return "error/paginaError";
        }
    }



}
