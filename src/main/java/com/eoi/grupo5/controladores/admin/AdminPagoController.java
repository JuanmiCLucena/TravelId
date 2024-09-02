package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Pago;
import com.eoi.grupo5.modelos.MetodoPago;
import com.eoi.grupo5.modelos.Reserva;
import com.eoi.grupo5.paginacion.PaginaRespuestaPagos;
import com.eoi.grupo5.servicios.ServicioMetodoPago;
import com.eoi.grupo5.servicios.ServicioPago;
import com.eoi.grupo5.servicios.ServicioReserva;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador para gestionar las operaciones de administración relacionadas con los pagos.
 * Permite crear, listar, editar y eliminar pagos desde la interfaz de administración.
 */
@Controller
@RequestMapping("/admin/pagos")
public class AdminPagoController {

    private final ServicioPago servicioPago;
    private final ServicioReserva servicioReserva;
    private final ServicioMetodoPago servicioMetodoPago;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param servicioReserva Servicio para manejar las operaciones de reserva.
     * @param servicioPago Servicio para manejar las operaciones de pago.
     * @param servicioMetodoPago Servicio para manejar los métodos de pago.
     */
    public AdminPagoController(ServicioReserva servicioReserva, ServicioPago servicioPago, ServicioMetodoPago servicioMetodoPago) {
        this.servicioReserva = servicioReserva;
        this.servicioPago = servicioPago;
        this.servicioMetodoPago = servicioMetodoPago;
    }

    /**
     * Maneja la solicitud GET para listar todos los pagos paginados.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param page Número de la página solicitada (por defecto 0).
     * @param size Tamaño de la página (por defecto 10).
     * @return El nombre de la plantilla que muestra la lista de pagos.
     */
    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaPagos<Pago> pagosPage = servicioPago.buscarEntidadesPaginadas(page, size);
        modelo.addAttribute("pagos", pagosPage.getContent());
        modelo.addAttribute("page", pagosPage);
        return "admin/pagos/adminPagos";
    }

    /**
     * Muestra la página para crear un nuevo pago.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra el formulario de creación de pago.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        modelo.addAttribute("pago", new Pago());
        modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
        modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
        return "admin/pagos/adminNuevoPago";
    }

    /**
     * Maneja la solicitud POST para crear un nuevo pago.
     *
     * @param pago El pago que se va a crear, validado.
     * @param result Resultado de la validación del pago.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de pagos o la vista de creación si hay errores.
     */
    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("pago") Pago pago, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
            return "admin/pagos/adminNuevoPago";
        }
        try {
            Optional<Reserva> reserva = servicioReserva.encuentraPorId(pago.getReserva().getId());
            Optional<MetodoPago> metodoPago = servicioMetodoPago.encuentraPorId(pago.getMetodoPago().getId());

            if (reserva.isPresent() && metodoPago.isPresent()) {
                pago.setReserva(reserva.get());
                reserva.get().setPago(pago);
                pago.setMetodoPago(metodoPago.get());
                servicioPago.guardar(pago);
            } else {
                modelo.addAttribute("error", "Reserva o método de pago no encontrados.");
                modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
                modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
                return "admin/pagos/adminNuevoPago";
            }
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al crear el pago: " + e.getMessage());
            modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
            return "admin/pagos/adminNuevoPago";
        }
        return "redirect:/admin/pagos";
    }

    /**
     * Muestra la página para editar un pago existente.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param id ID del pago que se va a editar.
     * @return El nombre de la plantilla que muestra el formulario de edición de pago, o una página de error si no se encuentra el pago.
     */
    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(Model modelo, @PathVariable Integer id) {
        Optional<Pago> pago = servicioPago.encuentraPorId(id);
        if (pago.isPresent()) {
            modelo.addAttribute("pago", pago.get());
            modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
            return "admin/pagos/adminDetallesPago";
        } else {
            return "pagoNoEncontrado";
        }
    }

    /**
     * Maneja la solicitud PUT para editar un pago existente.
     *
     * @param id ID del pago que se va a actualizar.
     * @param pago El pago actualizado, validado.
     * @param result Resultado de la validación del pago.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de pagos o la vista de edición si hay errores.
     */
    @PutMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, @Valid @ModelAttribute("pago") Pago pago, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
            return "admin/pagos/adminDetallesPago";
        }
        try {
            Optional<Reserva> reserva = servicioReserva.encuentraPorId(pago.getReserva().getId());
            Optional<MetodoPago> metodoPago = servicioMetodoPago.encuentraPorId(pago.getMetodoPago().getId());

            if (reserva.isPresent() && metodoPago.isPresent()) {
                pago.setId(id);
                pago.setReserva(reserva.get());
                pago.setMetodoPago(metodoPago.get());
                servicioPago.guardar(pago);
            } else {
                modelo.addAttribute("error", "Reserva o método de pago no encontrados.");
                modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
                modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
                return "admin/pagos/adminDetallesPago";
            }
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al editar el pago: " + e.getMessage());
            modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
            return "admin/pagos/adminDetallesPago";
        }
        return "redirect:/admin/pagos";
    }

    /**
     * Maneja la solicitud DELETE para eliminar un pago existente.
     *
     * @param id ID del pago que se va a eliminar.
     * @return Redirección a la lista de pagos.
     */
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Pago> optionalPago = servicioPago.encuentraPorId(id);
        if (optionalPago.isPresent()) {
            servicioPago.eliminarPorId(id);
        }
        return "redirect:/admin/pagos";
    }
}
