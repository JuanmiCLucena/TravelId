package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.servicios.ServicioAsiento;
import com.eoi.grupo5.servicios.ServicioVuelo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class VueloController {

    private final ServicioVuelo servicioVuelo;

    private final ServicioAsiento servicioAsiento;

    public VueloController(ServicioVuelo servicioVuelo, ServicioAsiento servicioAsiento) {
        this.servicioVuelo = servicioVuelo;
        this.servicioAsiento = servicioAsiento;
    }

    @GetMapping("vuelos/lista")
    public String listaVuelos(Model modelo) {

        int page = 0;
        int size = 6;

        LocalDateTime fechaActual = LocalDateTime.now();

        // Obtener los vuelos disponibles paginados
        PaginaRespuestaVuelos<Vuelo> vuelosPaginados = servicioVuelo.obtenerVuelosDisponiblesPaginados(page, size, fechaActual);
        modelo.addAttribute("vuelos", vuelosPaginados.getContent());
        modelo.addAttribute("totalPages", vuelosPaginados.getTotalPages());
        modelo.addAttribute("currentPage", vuelosPaginados.getPage());
        return "vuelos";
    }

    @GetMapping("/vuelo/{id}")
    public String detallesHotel(Model modelo, @PathVariable Integer id) {
        Optional<Vuelo> vuelo = servicioVuelo.encuentraPorId(id);
        // Si no encontramos el vuelo no hemos encontrado el hotel
        if(vuelo.isPresent()) {
            if(vuelo.get().getImagen() != null) {
            String vueloImagen = vuelo.get().getImagen().getUrl();
                modelo.addAttribute("imagenVuelo", vueloImagen);
            }
            //modelo.addAttribute("recomendados", servicioVuelo.obtenerHotelesEnTuZona(vuelo.get()));
            modelo.addAttribute("vuelo",vuelo.get());
            modelo.addAttribute("preciosActuales",
                    servicioAsiento.obtenerPreciosActualesAsientosVuelo(vuelo.get()));

            return "detallesVuelo";
        } else {
            // Vuelo no encontrado - htlm
            return "vueloNoEncontrado";
        }

    }

}
