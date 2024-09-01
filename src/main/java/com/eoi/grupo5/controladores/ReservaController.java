package com.eoi.grupo5.controladores;

import com.eoi.grupo5.email.CustomEmailService;
import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaReservas;
import com.eoi.grupo5.repos.RepoUsuario;
import com.eoi.grupo5.servicios.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
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

/**
 * El controlador `ReservaController` gestiona las operaciones relacionadas con las reservas en la aplicación,
 * abarcando una variedad de funcionalidades, desde la visualización y gestión de reservas de habitaciones y actividades,
 * hasta la creación y cancelación de reservas.
 *
 * Este controlador se encarga de interactuar con los servicios y repositorios
 * que manejan la lógica de negocio y la persistencia de datos, así como de enviar notificaciones por correo electrónico
 * a los usuarios sobre el estado de sus reservas.
 *
 * Funcionalidades principales:
 * - **Visualización de reservas**: Permite a los usuarios autenticados ver y gestionar sus reservas existentes.
 * - **Creación de reservas de habitaciones**: Facilita la reserva de habitaciones, mostrando disponibilidad y gestionando
 *   detalles de la reserva.
 * - **Creación de reservas de actividades**: Permite a los usuarios reservar actividades, verificar la disponibilidad
 *   de plazas y gestionar la confirmación de reservas.
 * - **Cancelación de reservas**: Proporciona la capacidad para cancelar reservas existentes y actualizar el estado
 *   de las actividades asociadas.
 * - **Notificación por correo electrónico**: Envía correos electrónicos de confirmación y cancelación de reservas
 *   a los usuarios con los detalles pertinentes.
 *
 * Dependencias:
 * - {@link ServicioReserva}: Servicio que maneja la lógica de negocio de las reservas.
 * - {@link ServicioHabitacion}: Servicio que gestiona la lógica de negocio de las habitaciones.
 * - {@link ServicioActividad}: Servicio que gestiona la lógica de negocio de las actividades.
 * - {@link RepoUsuario}: Repositorio para acceder y gestionar los datos de los usuarios.
 * - {@link ServicioMetodoPago}: Servicio que gestiona los métodos de pago.
 * - {@link CustomEmailService}: Servicio para el envío de correos electrónicos.
 *
 * @see ServicioReserva
 * @see ServicioHabitacion
 * @see ServicioActividad
 * @see RepoUsuario
 * @see ServicioMetodoPago
 * @see CustomEmailService
 */

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

    /**
     * Constructor de `ReservaController` con inyección de dependencias.
     *
     * @param servicioReserva Servicio para gestionar las reservas.
     * @param servicioHabitacion Servicio para gestionar las habitaciones.
     * @param servicioActividad Servicio para gestionar las actividades.
     * @param repoUsuario Repositorio para gestionar los usuarios.
     * @param servicioMetodoPago Servicio para gestionar los métodos de pago.
     * @param emailService Servicio para enviar correos electrónicos.
     */
    public ReservaController(ServicioReserva servicioReserva, ServicioHabitacion servicioHabitacion,
                             ServicioActividad servicioActividad, RepoUsuario repoUsuario,
                             ServicioMetodoPago servicioMetodoPago, CustomEmailService emailService) {

        this.servicioReserva = servicioReserva;
        this.servicioHabitacion = servicioHabitacion;
        this.servicioActividad = servicioActividad;
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
        this.repoUsuario = repoUsuario;
        this.servicioMetodoPago = servicioMetodoPago;
        this.emailService = emailService;
    }

    /**
     * Muestra las reservas del usuario autenticado.
     *
     * Este método obtiene el usuario autenticado a través del objeto {@link Principal},
     * luego busca las reservas asociadas a ese usuario en la base de datos, y las muestra en una vista paginada.
     *
     * @param principal El objeto {@link Principal} que representa al usuario autenticado.
     * @param modelo El objeto {@link Model} que se utiliza para pasar datos a la vista.
     * @param page El número de página para la paginación. Por defecto es 0.
     * @param size El tamaño de la página para la paginación. Por defecto es 10.
     * @return La vista "reservas/misReservas" que muestra las reservas del usuario autenticado.
     */
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

    /**
     * Muestra la página para crear una reserva de una habitación específica.
     *
     * Este método obtiene una habitación específica basada en el ID proporcionado, verifica si la habitación existe,
     * y luego la muestra junto con su precio y una imagen asociada, si están disponibles.
     *
     * @param modelo El objeto {@link Model} que se utiliza para pasar datos a la vista.
     * @param id El ID de la habitación que se desea reservar.
     * @return La vista "reservas/habitacion/disponibilidadHabitacion" que muestra la disponibilidad y detalles de la habitación.
     */
    @GetMapping("/habitacion/reservar/{id}")
    public String mostrarPaginaCrear(Model modelo, @PathVariable Integer id) {
        Optional<Habitacion> optionalHabitacion = servicioHabitacion.encuentraPorId(id);
        if (optionalHabitacion.isPresent()) {
            Habitacion habitacion = optionalHabitacion.get();
            modelo.addAttribute("habitacion", habitacion);

            // Obtener y mostrar el precio actual si está disponible
            if (!habitacion.getPrecio().isEmpty()) {
                Precio precioActual = servicioHabitacion.getPrecioActual(habitacion, LocalDateTime.now());
                modelo.addAttribute("precioActual", precioActual.getValor());
            }

            // Obtener y mostrar la primera imagen de la habitación si está disponible
            if (!habitacion.getImagenesHabitacion().isEmpty()) {
                String habitacionImagen = habitacion.getImagenesHabitacion().stream().findFirst().get().getUrl();
                modelo.addAttribute("imagenHabitacion", habitacionImagen);
            }

        } else {
            modelo.addAttribute("error", "La habitación no se encuentra.");
            return "error/paginaError";  // O la vista que maneja errores
        }
        return "reservas/habitacion/disponibilidadHabitacion";
    }

    /**
     * Crea una nueva reserva para el usuario autenticado.
     *
     * Este método toma las fechas de inicio y fin de la reserva, el usuario autenticado,
     * y crea una nueva reserva en el sistema. Si la creación es exitosa, redirige al usuario a la página principal
     * de reservas. En caso de error, redirige al usuario a la página de inicio de sesión con un mensaje de error.
     *
     * @param fechaInicio La fecha de inicio de la reserva en formato ISO (yyyy-MM-ddTHH:mm:ss).
     * @param fechaFin La fecha de fin de la reserva en formato ISO (yyyy-MM-ddTHH:mm:ss).
     * @param principal El objeto {@link Principal} que representa al usuario autenticado.
     * @param model El objeto {@link Model} utilizado para pasar datos a la vista.
     * @return Una redirección a la página principal de reservas en caso de éxito, o a la página de inicio de sesión en caso de error.
     */
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

    /**
     * Obtiene los rangos de fechas disponibles para una habitación específica.
     *
     * Este método valida las fechas de inicio y fin para asegurarse de que sean posteriores a la fecha actual
     * y que la fecha de inicio no sea posterior a la fecha de fin. Luego, obtiene los rangos de disponibilidad de la habitación
     * y los métodos de pago disponibles, y los pasa a la vista para ser mostrados al usuario.
     *
     * @param idHabitacion El ID de la habitación para la cual se desea verificar la disponibilidad.
     * @param fechaInicio La fecha de inicio en formato ISO (yyyy-MM-ddTHH:mm:ss).
     * @param fechaFin La fecha de fin en formato ISO (yyyy-MM-ddTHH:mm:ss).
     * @param modelo El objeto {@link Model} utilizado para pasar datos a la vista.
     * @param redirectAttributes El objeto {@link RedirectAttributes} utilizado para pasar mensajes en caso de redirección.
     * @return La vista "reservas/habitacion/reservarHabitacion" que muestra la disponibilidad de la habitación, o una redirección en caso de error.
     */
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

        //Obtener la habitación
        Optional<Habitacion> optionalHabitacion = servicioHabitacion.encuentraPorId(idHabitacion);

        Habitacion habitacion = null;
        if (optionalHabitacion.isPresent()) {
            habitacion = optionalHabitacion.get();
        }

        //Obtener los rangos de disponibilidad
        List<ServicioHabitacion.Interval> rangosDisponibles = servicioHabitacion.obtenerRangosDisponibles(idHabitacion, fechaInicio, fechaFin);
        modelo.addAttribute("rangosDisponibles", rangosDisponibles);
        modelo.addAttribute("habitacion", habitacion);
        modelo.addAttribute("fechaInicio", fechaInicio);
        modelo.addAttribute("fechaFin", fechaFin);

        //Obtener métodos de pago disponibles
        List<MetodoPago> metodosPago = servicioMetodoPago.buscarEntidades();
        modelo.addAttribute("metodosPago", metodosPago);

        return "reservas/habitacion/reservarHabitacion";
    }

    /**
     * Realiza la reserva de una habitación y procesa el pago correspondiente.
     *
     * Este método maneja la lógica para que un usuario autenticado pueda reservar una habitación específica,
     * procesando el pago y enviando un correo electrónico de confirmación con los detalles de la reserva.
     *
     * @param idHabitacion El ID de la habitación que se desea reservar.
     * @param fechaInicio La fecha y hora de inicio de la reserva en formato ISO (yyyy-MM-ddTHH:mm:ss).
     * @param fechaFin La fecha y hora de fin de la reserva en formato ISO (yyyy-MM-ddTHH:mm:ss).
     * @param precioTotal El costo total de la reserva.
     * @param metodoPagoId El ID del método de pago seleccionado para realizar la transacción.
     * @param principal El objeto {@link Principal} que representa al usuario autenticado.
     * @param modelo El objeto {@link Model} utilizado para pasar datos a la vista en caso de error.
     * @return Una redirección a la página de "Mis Reservas" en caso de éxito, o una página de error en caso de que ocurra una excepción.
     *
     * @throws Exception En caso de que ocurra algún error durante la creación de la reserva o el procesamiento del pago.
     */
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
                // Crear la reserva con las fechas proporcionadas
                Reserva reserva = servicioReserva.crearReserva(usuario, fechaInicio, fechaFin);
                // Asignar la habitación a la reserva
                servicioReserva.addHabitacion(reserva, idHabitacion, fechaInicio, fechaFin);
                // Generar el pago de la reserva
                servicioReserva.generarPago(reserva, precioTotal, metodoPagoId);

                // Formatear las fechas para el correo electrónico
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm");
                String fechaInicioFormateada = reserva.getFechaInicio().format(formatter);
                String fechaFinFormateada = reserva.getFechaFin().format(formatter);

                // Enviar el correo de confirmación al usuario
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

                // Redirigir al usuario a la página de "Mis Reservas"
                return "redirect:/reservas/mis-reservas";
            } catch (Exception e) {
                // Manejar excepciones y mostrar la página de error
                modelo.addAttribute("error", e.getMessage());
                return "error/paginaError";
            }
        } else {
            // Manejar el caso en que el usuario no se encuentre
            modelo.addAttribute("error", "Usuario no encontrado");
            return "error/paginaError";
        }
    }

    /**
     * Realiza la reserva de una actividad y procesa el pago correspondiente.
     *
     * Este método maneja la lógica para que un usuario autenticado pueda reservar una actividad específica,
     * procesar el pago, y enviar un correo electrónico de confirmación con los detalles de la reserva.
     *
     * @param idActividad El ID de la actividad que se desea reservar.
     * @param fechaInicio La fecha y hora de inicio de la reserva en formato ISO (yyyy-MM-ddTHH:mm:ss).
     * @param fechaFin La fecha y hora de fin de la reserva en formato ISO (yyyy-MM-ddTHH:mm:ss).
     * @param asistentes El número de asistentes para la actividad.
     * @param precioTotal El costo total de la reserva para la actividad.
     * @param metodoPagoId El ID del método de pago seleccionado para realizar la transacción.
     * @param principal El objeto {@link Principal} que representa al usuario autenticado.
     * @param modelo El objeto {@link Model} utilizado para pasar datos a la vista en caso de error.
     * @param redirectAttributes El objeto {@link RedirectAttributes} para redirigir con atributos de error.
     * @param session El objeto {@link HttpSession} utilizado para mantener información durante la sesión.
     * @return Una redirección a la página de "Mis Reservas" en caso de éxito, o una página de error en caso de que ocurra una excepción.
     *
     * @throws UsernameNotFoundException Si el usuario autenticado no es encontrado.
     * @throws EntityNotFoundException Si la actividad especificada no es encontrada.
     * @throws Exception En caso de que ocurra algún error durante la creación de la reserva o el procesamiento del pago.
     */
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

            // Formatear las fechas para el correo electrónico
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy HH:mm");
            String fechaInicioFormateada = reserva.getFechaInicio().format(formatter);
            String fechaFinFormateada = reserva.getFechaFin().format(formatter);

            // Enviar el correo de confirmación al usuario
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
            // Redirigir al usuario a la página de "Mis Reservas"
            return "redirect:/reservas/mis-reservas";
        } catch (Exception e) {
            // Manejar excepciones y mostrar la página de error
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





    /**
     * Cancela una reserva existente y actualiza los detalles correspondientes.
     *
     * Este método maneja la cancelación de una reserva identificada por su ID, actualiza la cantidad de asistentes en las
     * actividades asociadas a la reserva y envía un correo electrónico de confirmación de la cancelación al usuario.
     *
     * @param id El ID de la reserva que se desea cancelar.
     * @param modelo El objeto {@link Model} utilizado para pasar datos a la vista en caso de error.
     * @return Una redirección a la página de "Mis Reservas" en caso de éxito, o una página de error en caso de que ocurra una excepción.
     *
     * @throws Exception En caso de que ocurra algún error durante el proceso de cancelación.
     */
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

                // Obtener detalles de la reserva cancelada
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
                // Redirigir al usuario a la página de "Mis Reservas"
                return "redirect:/reservas/mis-reservas";
            } else {
                // Manejar el caso en que la reserva no se encuentra
                modelo.addAttribute("error", "Reserva no encontrada");
                return "error/paginaError";
            }
        } catch (Exception e) {
            // Manejar excepciones y mostrar la página de error
            modelo.addAttribute("error", e.getMessage());
            return "error/paginaError";
        }
    }

}
