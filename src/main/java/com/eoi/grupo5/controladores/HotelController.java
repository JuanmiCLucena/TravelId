package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.Hotel;

import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import com.eoi.grupo5.servicios.ServicioHotel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class HotelController {

    private final ServicioHotel servicioHotel;

    private final ServicioHabitacion servicioHabitacion;


    public HotelController(ServicioHotel servicioHotel, ServicioHabitacion servicioHabitacion) {
        this.servicioHotel = servicioHotel;
        this.servicioHabitacion = servicioHabitacion;
    }

    @GetMapping("/hoteles/lista")
    public String listaHoteles(Model modelo) {
        List<Hotel> hoteles = servicioHotel.buscarEntidades();
        modelo.addAttribute("hoteles",hoteles);
        return "hoteles";
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

        return "detallesHotel";
        } else {
            // Hotel no encontrado - htlm
            return "hotelNoEncontrado";
        }

    }

//    @PostMapping("/hotel/crear")
//    public String crearHotel(Model modelo) {
//        return "hoteles";
//    }

}
