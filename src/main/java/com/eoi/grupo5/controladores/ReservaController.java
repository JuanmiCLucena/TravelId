package com.eoi.grupo5.controladores;

import com.eoi.grupo5.email.CustomEmailService;
import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaReservas;
import com.eoi.grupo5.repos.RepoUsuario;
import com.eoi.grupo5.servicios.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ServicioReserva servicioReserva;
    private final ServicioHabitacion servicioHabitacion;
    private final ServicioActividad servicioActividad;
    private final ServicioVuelo servicioVuelo;
    private final ServicioAsiento servicioAsiento;
    private final RepoUsuario repoUsuario;
    private final ServicioMetodoPago servicioMetodoPago;
    private final CustomEmailService emailService;

    public ReservaController(ServicioReserva servicioReserva, ServicioHabitacion servicioHabitacion, ServicioActividad servicioActividad, ServicioVuelo servicioVuelo, ServicioAsiento servicioAsiento, RepoUsuario repoUsuario, ServicioMetodoPago servicioMetodoPago, CustomEmailService emailService) {
        this.servicioReserva = servicioReserva;
        this.servicioHabitacion = servicioHabitacion;
        this.servicioActividad = servicioActividad;
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
        this.repoUsuario = repoUsuario;
        this.servicioMetodoPago = servicioMetodoPago;
        this.emailService = emailService;
    }

    @GetMapping("/mis-reservas")
    public String verMisReservas(
            Principal principal,
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Obtener el usuario autenticado
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(principal.getName());
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            // Obtener las reservas del usuario
            PaginaRespuestaReservas<Reserva> reservasPage = servicioReserva.obtenerReservasPorUsuarioPaginadas(usuario, page, size);
            List<Reserva> reservas = reservasPage.getContent();
            modelo.addAttribute("reservas", reservas);
            modelo.addAttribute("page", reservasPage);

            return "reservas/misReservas";
        } else {
            modelo.addAttribute("error", "Usuario no encontrado");
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
        if (fechaInicio.isBefore(fechaActual.minusMinutes(1)) || fechaFin.isBefore(fechaActual)) {
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

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm");

                String fechaInicioFormateada = reserva.getFechaInicio().format(formatter);
                String fechaFinFormateada = reserva.getFechaFin().format(formatter);

                emailService.sendSimpleMessage(
                        usuario.getDetalles().getEmail(),
                        "Confirmación de tu reserva en TravelId",
                        "Hola " + usuario.getNombreUsuario() + ",\n\n" +
                                "Gracias por realizar tu reserva con TravelId. A continuación, encontrarás los detalles de tu reserva:\n\n" +
                                "Usuario: " + usuario.getNombreUsuario() + "\n" +
                                "Email: " + usuario.getDetalles().getEmail() + "\n" +
                                "Habitación: " + servicioHabitacion.encuentraPorId(idHabitacion).get().getNumero() + "\n" +
                                "Fecha de la reserva:" + fechaInicioFormateada + " hasta " + fechaFinFormateada + "\n" +
                                "Importe: " + reserva.getPago().getImporte() + "\n" +
                                "Si tienes alguna pregunta o necesitas asistencia adicional, no dudes en contactarnos.\n\n" +
                                "¡Gracias por confiar en TravelId!\n\n" +
                                "Saludos cordiales,\n" +
                                "El equipo de TravelId"
                );


                return "redirect:/reservas/mis-reservas";
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
            @RequestParam("asistentes") Integer asistentes,
            @RequestParam("precioTotal") Double precioTotal,
            @RequestParam("metodoPagoId") Integer metodoPagoId,
            Principal principal,
            Model modelo,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        // Obtener el usuario autenticado
        Usuario usuario = repoUsuario.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        try {
            // Obtener la actividad
            Actividad actividad = servicioActividad.encuentraPorId(idActividad)
                    .orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada"));

            // Verificar disponibilidad de plazas
            Integer plazasDisponibles = actividad.getMaximosAsistentes() - actividad.getAsistentesConfirmados();
            if (actividad.getAsistentesConfirmados() + asistentes > actividad.getMaximosAsistentes()) {
                redirectAttributes.addFlashAttribute("error", "No hay suficientes plazas!");
                redirectAttributes.addFlashAttribute("plazasDisponibles", plazasDisponibles);
                return "redirect:/actividad/" + idActividad;
            }

            // Crear la reserva
            Reserva reserva = servicioReserva.crearReserva(usuario, fechaInicio, fechaFin);
            servicioReserva.addActividad(reserva, idActividad, fechaInicio, fechaFin, asistentes);

            // Generar el pago
            servicioReserva.generarPago(reserva, precioTotal, metodoPagoId);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm");

            String fechaInicioFormateada = reserva.getFechaInicio().format(formatter);
            String fechaFinFormateada = reserva.getFechaFin().format(formatter);

            emailService.sendSimpleMessage(
                    usuario.getDetalles().getEmail(),
                    "Confirmación de tu reserva en TravelId",
                    "Hola " + usuario.getNombreUsuario() + ",\n\n" +
                            "Gracias por realizar tu reserva con TravelId. A continuación, encontrarás los detalles de tu reserva:\n\n" +
                            "Usuario: " + usuario.getNombreUsuario() + "\n" +
                            "Email: " + usuario.getDetalles().getEmail() + "\n" +
                            "Actividad: " + actividad.getNombre() + "\n" +
                            "Fecha de la reserva:" + fechaInicioFormateada + " hasta " + fechaFinFormateada + "\n" +
                            "Plazas Reservadas: " + asistentes + "\n" +
                            "Importe por plaza: " + precioTotal/asistentes + "\n" +
                            "Importe Total: " + reserva.getPago().getImporte() + "\n" +
                            "Si tienes alguna pregunta o necesitas asistencia adicional, no dudes en contactarnos.\n\n" +
                            "¡Gracias por confiar en TravelId!\n\n" +
                            "Saludos cordiales,\n" +
                            "El equipo de TravelId"
            );

            return "redirect:/reservas/mis-reservas";
        } catch (Exception e) {
            modelo.addAttribute("error", e.getMessage());
            return "error/paginaError";
        }
    }

    @PostMapping("/asiento/reservar")
    public String reservarAsiento(
            @RequestParam("idAsiento") Integer idAsiento,
            @RequestParam("idVuelo") Integer idVuelo,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam("precioTotal") Double precioTotal,
            @RequestParam("metodoPagoId") Integer metodoPagoId,
            Principal principal,
            RedirectAttributes redirectAttributes,
            Model modelo) {

        // Obtener el usuario autenticado
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(principal.getName());
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();

            try {
                // Verificar si el asiento existe
                Optional<Asiento> optionalAsiento = servicioAsiento.encuentraPorId(idAsiento);
                if (optionalAsiento.isPresent()) {
                    Asiento asiento = optionalAsiento.get();

                    // Verificar si el asiento ya está reservado
                    boolean reservado = asiento.getReservas().stream()
                            .anyMatch(reserva -> !reserva.isCancelado());
                    if (reservado) {
                        redirectAttributes.addFlashAttribute("error", "El asiento ya ha sido reservado.");
                        return "redirect:/vuelo/" + idVuelo;
                    }

                    // Crear la reserva ya que el asiento está disponible
                    Reserva reserva = servicioReserva.crearReserva(usuario, fechaInicio, fechaFin);

                    // Asignar el asiento a la reserva
                    servicioReserva.addAsiento(reserva.getId(), idAsiento);

                    // Generar el pago
                    servicioReserva.generarPago(reserva, precioTotal, metodoPagoId);

                    // Guardar el asiento actualizado
                    servicioAsiento.guardar(asiento);

                    // Enviar correo de confirmación
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm");
                    String fechaInicioFormateada = reserva.getFechaInicio().format(formatter);
                    String fechaFinFormateada = reserva.getFechaFin().format(formatter);

                    emailService.sendSimpleMessage(
                            usuario.getDetalles().getEmail(),
                            "Confirmación de tu reserva en TravelId",
                            "Hola " + usuario.getNombreUsuario() + ",\n\n" +
                                    "Gracias por realizar tu reserva con TravelId. A continuación, encontrarás los detalles de tu reserva:\n\n" +
                                    "Usuario: " + usuario.getNombreUsuario() + "\n" +
                                    "Email: " + usuario.getDetalles().getEmail() + "\n" +
                                    "Vuelo: " + servicioVuelo.encuentraPorId(idVuelo).get().getNombre() + "\n" +
                                    "Fecha de la reserva: " + fechaInicioFormateada + " hasta " + fechaFinFormateada + "\n" +
                                    "Asiento: " + asiento.getNumero() + "\n" +
                                    "Importe Total: " + reserva.getPago().getImporte() + "\n" +
                                    "Si tienes alguna pregunta o necesitas asistencia adicional, no dudes en contactarnos.\n\n" +
                                    "¡Gracias por confiar en TravelId!\n\n" +
                                    "Saludos cordiales,\n" +
                                    "El equipo de TravelId"
                    );

                    return "redirect:/reservas/mis-reservas";
                } else {
                    redirectAttributes.addFlashAttribute("error", "Asiento no encontrado.");
                    return "redirect:/vuelo/" + idVuelo;
                }

            } catch (Exception e) {
                modelo.addAttribute("error", e.getMessage());
                return "error/paginaError";
            }
        } else {
            modelo.addAttribute("error", "Usuario no encontrado");
            return "error/paginaError";
        }
    }




    @PostMapping("/cancelar/{id}")
    public String cancelarReserva(@PathVariable Integer id, Model modelo) {
        try {
            // Obtener la reserva por ID
            Optional<Reserva> optionalReserva = servicioReserva.encuentraPorId(id);
            if (optionalReserva.isPresent()) {
                Reserva reserva = optionalReserva.get();
                Usuario usuario = reserva.getUsu();

                // Cancelar la reserva
                servicioReserva.cancelarReserva(id);

                // Actualizar el número de asistentes en ReservaActividad
                for (ReservaActividad reservaActividad : reserva.getReservaActividades()) {
                    Actividad actividad = reservaActividad.getActividad();
                    Integer asistentesReservados = reservaActividad.getAsistentes();
                    actividad.setAsistentesConfirmados(actividad.getAsistentesConfirmados() - asistentesReservados);
                    // Actualizar la actividad en el repositorio
                    servicioActividad.guardar(actividad);
                }

                // Formatear las fechas de la reserva
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm");
                String fechaInicioFormateada = reserva.getFechaInicio().format(formatter);
                String fechaFinFormateada = reserva.getFechaFin().format(formatter);

                String detallesReserva = servicioReserva.obtenerDetallesReserva(reserva);

                // Enviar correo de confirmación de cancelación
                emailService.sendSimpleMessage(
                        usuario.getDetalles().getEmail(),
                        "Cancelación de tu reserva en TravelId",
                        "Hola " + usuario.getNombreUsuario() + ",\n\n" +
                                "Tu reserva en TravelId ha sido cancelada con éxito. A continuación, te proporcionamos los detalles de la reserva cancelada:\n\n" +
                                "Usuario: " + usuario.getNombreUsuario() + "\n" +
                                "Email: " + usuario.getDetalles().getEmail() + "\n" +
                                detallesReserva +
                                "Fecha de la reserva: " + fechaInicioFormateada + " hasta " + fechaFinFormateada + "\n" +
                                "Si tienes alguna pregunta o necesitas asistencia adicional, no dudes en contactarnos.\n\n" +
                                "Lamentamos cualquier inconveniente y esperamos poder servirte en el futuro.\n\n" +
                                "Saludos cordiales,\n" +
                                "El equipo de TravelId"
                );

                return "redirect:/reservas/mis-reservas";
            } else {
                modelo.addAttribute("error", "Reserva no encontrada");
                return "error/paginaError";
            }
        } catch (Exception e) {
            modelo.addAttribute("error", e.getMessage());
            return "error/paginaError";
        }
    }





}
