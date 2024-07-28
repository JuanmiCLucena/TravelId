package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.Pago;
import com.eoi.grupo5.paginacion.PaginaRespuestaPagos;
import com.eoi.grupo5.servicios.ServicioMetodoPago;
import com.eoi.grupo5.servicios.ServicioPago;
import com.eoi.grupo5.servicios.ServicioReserva;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/pagos")
public class AdminPagoController {

    private final ServicioPago servicioPago;
    private final ServicioReserva servicioReserva;
    private final ServicioMetodoPago servicioMetodoPago;

    public AdminPagoController(ServicioReserva servicioReserva, ServicioPago servicioPago, ServicioMetodoPago servicioMetodoPago) {
        this.servicioReserva = servicioReserva;
        this.servicioPago = servicioPago;
        this.servicioMetodoPago = servicioMetodoPago;
    }

    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaPagos<Pago> pagosPage = servicioPago.buscarEntidadesPaginadas(page, size);
        List<Pago> pagos = servicioPago.buscarEntidades();
        modelo.addAttribute("pagos",pagos);
        modelo.addAttribute("page", pagosPage);
        return "admin/adminPagos";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Pago> pago = servicioPago.encuentraPorId(id);
        if(pago.isPresent()) {
            modelo.addAttribute("pago",pago.get());
            modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());

            return "admin/adminDetallesPago";
        } else {
            // Pago no encontrado - htlm
            return "PagoNoEncontrada";
        }

    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Pago pago = new Pago();
        modelo.addAttribute("pago", pago);
        modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
        modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
        return "admin/adminNuevoPago";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("pago") Pago pago, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("reservas", servicioReserva.buscarEntidades());
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());
            return "admin/adminNuevoPago";
        }
        try {
            servicioPago.guardar(pago);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/admin/pagos";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Pago> optionalPago = servicioPago.encuentraPorId(id);
        if(optionalPago.isPresent()) {
            servicioPago.eliminarPorId(id);
        }
        return "redirect:/admin/pagos";
    }

}
