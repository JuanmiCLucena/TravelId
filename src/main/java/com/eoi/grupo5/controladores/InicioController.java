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

import java.time.LocalDateTime;

/**
 * El controlador `InicioController` maneja las solicitudes relacionadas con la página principal de la aplicación y la página de "Conocenos".
 * Este controlador proporciona la lógica para mostrar las principales ofertas de vuelos, hoteles y actividades en la página de inicio,
 * así como la información sobre la empresa en la página de "Conocenos".
 *
 * Funcionalidades principales:
 * - **Página de Inicio**: Muestra una vista consolidada con los vuelos, hoteles y actividades más recientes o disponibles.
 * - **Página de Conocenos**: Proporciona información sobre la empresa.
 *
 * Dependencias:
 * - {@link ServicioVuelo}: Servicio que maneja la lógica de negocio de los vuelos, incluyendo la obtención de vuelos disponibles.
 * - {@link ServicioHotel}: Servicio que gestiona la lógica de negocio de los hoteles, incluyendo la búsqueda de hoteles.
 * - {@link ServicioActividad}: Servicio que gestiona la lógica de negocio de las actividades, incluyendo la obtención de actividades disponibles.
 *
 * @see ServicioVuelo
 * @see ServicioHotel
 * @see ServicioActividad
 */
@Controller
public class InicioController {

    private final ServicioVuelo servicioVuelo;
    private final ServicioHotel servicioHotel;
    private final ServicioActividad servicioActividad;

    /**
     * Constructor para inyectar los servicios necesarios.
     *
     * @param servicioVuelo Servicio que maneja la lógica de negocio de los vuelos.
     * @param servicioHotel Servicio que gestiona la lógica de negocio de los hoteles.
     * @param servicioActividad Servicio que gestiona la lógica de negocio de las actividades.
     */
    public InicioController(ServicioVuelo servicioVuelo, ServicioHotel servicioHotel, ServicioActividad servicioActividad) {
        this.servicioVuelo = servicioVuelo;
        this.servicioHotel = servicioHotel;
        this.servicioActividad = servicioActividad;
    }

    /**
     * Maneja la solicitud GET para la página principal de la aplicación.
     * Obtiene y muestra una lista paginada de vuelos, hoteles y actividades.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @return La vista de la página principal ("index").
     */
    @GetMapping("/")
    public String inicio(Model modelo) {

        int page = 0;
        int size = 4;

        LocalDateTime fechaActual = LocalDateTime.now();

        // Obtener los vuelos disponibles paginados
        PaginaRespuestaVuelos<Vuelo> vuelosPaginados = servicioVuelo.obtenerVuelosDisponiblesPaginados(page, size, fechaActual);
        modelo.addAttribute("vuelos", vuelosPaginados.getContent());

        // Obtener los hoteles disponibles paginado
        PaginaRespuestaHoteles<Hotel> hotelesPaginados = servicioHotel.buscarEntidadesPaginadas(page, size);
        modelo.addAttribute("hoteles", hotelesPaginados.getContent());


        // Obtener las actividades disponibles paginados
        PaginaRespuestaActividades<Actividad> actividadesPaginados = servicioActividad.obtenerActividadesDisponiblesPaginadas(page, size, fechaActual);
        modelo.addAttribute("actividades", actividadesPaginados.getContent());


        return "index";
    }

    /**
     * Maneja la solicitud GET para la página "Conocenos".
     * Proporciona información sobre la empresa.
     *
     * @return La vista de la página "Conocenos" ("conocenos/about").
     */
    @GetMapping("/conocenos")
    public String conocenos() {
        return "conocenos/about";
    }

}
