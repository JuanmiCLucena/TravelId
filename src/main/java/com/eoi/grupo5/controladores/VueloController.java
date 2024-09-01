package com.eoi.grupo5.controladores;

import com.eoi.grupo5.dtos.AsientoDto;
import com.eoi.grupo5.modelos.Asiento;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.servicios.ServicioAsiento;
import com.eoi.grupo5.servicios.ServicioMetodoPago;
import com.eoi.grupo5.servicios.ServicioVuelo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class VueloController {

    private final ServicioVuelo servicioVuelo;
    private final ServicioAsiento servicioAsiento;
    private final ServicioMetodoPago servicioMetodoPago;

    public VueloController(ServicioVuelo servicioVuelo, ServicioAsiento servicioAsiento, ServicioMetodoPago servicioMetodoPago) {
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
        this.servicioMetodoPago = servicioMetodoPago;
    }

    @GetMapping("vuelos/lista")
    public String listaVuelos(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        LocalDateTime fechaActual = LocalDateTime.now();

        PaginaRespuestaVuelos<Vuelo> vuelosPage = servicioVuelo.obtenerVuelosDisponiblesPaginados(page, size,fechaActual);
        List<Vuelo> vuelos = vuelosPage.getContent();
        modelo.addAttribute("lista", vuelos);
        modelo.addAttribute("page", vuelosPage);
        return "vuelos/listaVuelos";
    }

    @GetMapping("/vuelo/{id}")
    public String detallesVuelo(Model modelo, @PathVariable Integer id) {
        Optional<Vuelo> vuelo = servicioVuelo.encuentraPorId(id);
        // Si no encontramos el vuelo no hemos encontrado el hotel
        if(vuelo.isPresent()) {
            if(vuelo.get().getImagen() != null) {
                String vueloImagen = vuelo.get().getImagen().getUrl();
                modelo.addAttribute("imagenVuelo", vueloImagen);
            }
            //modelo.addAttribute("recomendados", servicioVuelo.obtenerHotelesEnTuZona(vuelo.get()));
            modelo.addAttribute("vuelo",vuelo.get());

            LocalDateTime fechaActual = LocalDateTime.now();

            // Filtrar y agrupar asientos por categoría, solo si tienen un precio válido
            Map<String, List<AsientoDto>> asientosPorCategoria = vuelo.get().getAsientos().stream()
                    .map(asiento -> {
                        double precio = 0.0;
                        // Obtener el precio del asiento
                        var precioActual = servicioAsiento.getPrecioActual(asiento, fechaActual);
                        if (precioActual != null) {
                            precio = precioActual.getValor();
                        }

                        // Verificar si el asiento está reservado y si la reserva está cancelada
                        boolean reservado = asiento.getReservas().stream()
                                .anyMatch(reserva -> !reserva.isCancelado());

                        // Crear el objeto AsientoDto
                        return new AsientoDto(
                                asiento.getId(),
                                asiento.getNumero(),
                                reservado, // Asiento reservado si tiene una reserva activa
                                asiento.getCategoria().getNombre().toLowerCase(),
                                precio
                        );
                    })
                    .filter(asientoDto -> asientoDto.getPrecio() > 0)
                    .sorted(Comparator.comparing(AsientoDto::getNumero))
                    .collect(Collectors.groupingBy(AsientoDto::getCategoria, LinkedHashMap::new, Collectors.toList()));

            modelo.addAttribute("asientosPorCategoria", asientosPorCategoria);
            modelo.addAttribute("preciosActuales",
                    servicioAsiento.obtenerPreciosActualesAsientosVuelo(vuelo.get()));
            modelo.addAttribute("metodosPago", servicioMetodoPago.buscarEntidades());

            return "vuelos/detallesVuelo";
        } else {
            // Vuelo no encontrado - htlm
            return "error/paginaError";
        }

    }

}