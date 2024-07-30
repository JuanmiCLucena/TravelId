package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.servicios.ServicioActividad;
import com.eoi.grupo5.servicios.ServicioHotel;
import com.eoi.grupo5.servicios.ServicioVuelo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
public class InicioController {

    private final ServicioVuelo servicioVuelo;
    private final ServicioHotel servicioHotel;
    private final ServicioActividad servicioActividad;

    public InicioController(ServicioVuelo servicioVuelo, ServicioHotel servicioHotel, ServicioActividad servicioActividad) {
        this.servicioVuelo = servicioVuelo;
        this.servicioHotel = servicioHotel;
        this.servicioActividad = servicioActividad;
    }

    @GetMapping("/")
    public String inicio(Model modelo) {

        int page = 0;
        int size = 4;

        LocalDateTime fechaActual = LocalDateTime.now();

        // Obtener los vuelos disponibles paginados
        PaginaRespuestaVuelos<Vuelo> vuelosPaginados = servicioVuelo.obtenerVuelosDisponiblesPaginados(page, size, fechaActual);
        modelo.addAttribute("vuelos", vuelosPaginados.getContent());

        PaginaRespuestaHoteles<Hotel> hotelesPaginados = servicioHotel.buscarEntidadesPaginadas(page, size);
        modelo.addAttribute("hoteles", hotelesPaginados.getContent());


        // Obtener las actividades disponibles paginados
        PaginaRespuestaActividades<Actividad> actividadesPaginados = servicioActividad.obtenerActividadesDisponiblesPaginadas(page, size, fechaActual);
        modelo.addAttribute("actividades", actividadesPaginados.getContent());


        return "index";
    }

    @GetMapping("/conocenos")
    public String conocenos() {
        return "about";
    }

}
