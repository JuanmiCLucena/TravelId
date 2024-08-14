package com.eoi.grupo5.controladores;

import com.eoi.grupo5.dtos.HotelDto;
import com.eoi.grupo5.filtros.criteria.BusquedaCriteriaHoteles;
import com.eoi.grupo5.mapper.ActividadesMapper;
import com.eoi.grupo5.mapper.HotelesMapper;
import com.eoi.grupo5.modelos.Hotel;

import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.ServicioHotel;
import com.eoi.grupo5.servicios.ServicioTipoHabitacion;
import com.eoi.grupo5.servicios.filtros.ServicioFiltroActividades;
import com.eoi.grupo5.servicios.filtros.ServicioFiltroHoteles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HotelController {

    private final ServicioHotel servicioHotel;
    private final ServicioHabitacion servicioHabitacion;
    private final ServicioTipoHabitacion servicioTipoHabitacion;

    // Filtro
    private final ServicioFiltroHoteles servicioFiltroHoteles;
    private final HotelesMapper hotelesMapper;


    public HotelController(ServicioHotel servicioHotel, ServicioHabitacion servicioHabitacion, ServicioTipoHabitacion servicioTipoHabitacion, ServicioFiltroHoteles servicioFiltroHoteles, HotelesMapper hotelesMapper) {
        this.servicioHotel = servicioHotel;
        this.servicioHabitacion = servicioHabitacion;
        this.servicioTipoHabitacion = servicioTipoHabitacion;
        this.servicioFiltroHoteles = servicioFiltroHoteles;
        this.hotelesMapper = hotelesMapper;
    }

    @GetMapping("/hoteles/lista")
    public String listaHoteles(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());
        PaginaRespuestaHoteles<Hotel> hotelesPage = servicioHotel.buscarEntidadesPaginadas(page, size);
        List<Hotel> hoteles = hotelesPage.getContent();
        modelo.addAttribute("lista", hoteles);
        modelo.addAttribute("page", hotelesPage);
        return "hoteles/listaHoteles";
    }

    @GetMapping("/hotel/{id}")
    public String detallesHotel(Model modelo, @PathVariable Integer id) {
        Optional<Hotel> hotel = servicioHotel.encuentraPorId(id);
        // Si no encontramos el hotel no hemos encontrado el hotel
        if(hotel.isPresent()) {
            Optional<Imagen> optionalHotelImagen = hotel.get().getImagenesHotel().stream().findFirst();
            if(optionalHotelImagen.isPresent()) {
                String hotelImagen = optionalHotelImagen.get().getUrl();
                modelo.addAttribute("imagenHotel", hotelImagen);
            }
            modelo.addAttribute("recomendados", servicioHotel.obtenerHotelesEnTuZona(hotel.get()));
            modelo.addAttribute("hotel",hotel.get());
            modelo.addAttribute("preciosActuales",
                    servicioHabitacion.obtenerPreciosActualesHabitacionesHotel(hotel.get()));

        return "hoteles/detallesHotel";
        } else {
            // Hotel no encontrado - htlm
            return "hotelNoEncontrado";
        }

    }

    @GetMapping("/filtrar-hoteles")
    public String filtrarHoteles(Model modelo, BusquedaCriteriaHoteles criteria) {

        // Establecer tamaño de página predeterminado si no está definido o es inválido
        if (criteria.getSize() == null || criteria.getSize() <= 0) {
            criteria.setSize(6); // Definir un valor predeterminado de tamaño de página
        }

        // Realizar búsqueda de hoteles con los criterios de filtro
        PaginaRespuestaHoteles<HotelDto> hoteles = servicioFiltroHoteles
                .buscarHoteles(hotelesMapper.filtrar(criteria), criteria.getPage(), criteria.getSize());

        // Añadir los resultados de la búsqueda y otros atributos al modelo
        modelo.addAttribute("page", hoteles);
        modelo.addAttribute("lista", hoteles.getContent());

        // Añadir los valores de los criterios de búsqueda al modelo para mantener los filtros en la vista
        modelo.addAttribute("categoria", criteria.getCategoria());
        modelo.addAttribute("tipoHabitacionId", criteria.getTipoHabitacionId());
        modelo.addAttribute("capacidadHabitacion", criteria.getCapacidadHabitacion());
        modelo.addAttribute("localizacionNombre", criteria.getLocalizacionNombre());


        // Cargar otros datos necesarios para la vista, como los tipos de habitación disponibles
        modelo.addAttribute("tiposHabitacion", servicioTipoHabitacion.buscarEntidades());

        return "hoteles/listaHoteles"; // Redirigir a la vista con los hoteles filtrados
    }


}
