package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.Habitacion;
import com.eoi.grupo5.modelos.Precio;
import com.eoi.grupo5.servicios.ServicioHabitacion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class HabitacionController {

    private final ServicioHabitacion servicioHabitacion;

    public HabitacionController(ServicioHabitacion servicioHabitacion) {
        this.servicioHabitacion = servicioHabitacion;
    }

    @GetMapping("/habitacion/{id}")
    public String detallesHabitacion(@PathVariable Integer id, Model modelo) {
        Optional<Habitacion> habitacion = servicioHabitacion.encuentraPorId(id);
        // Si no encontramos el hotel no hemos encontrado el hotel
        if (habitacion.isPresent()) {

            modelo.addAttribute("habitacion", habitacion.get());

            if(!habitacion.get().getPrecio().isEmpty()) {
            Precio precioActual = servicioHabitacion.getPrecioActual(habitacion.get(), LocalDateTime.now());
            modelo.addAttribute("precioActual", precioActual.getValor());
            }

            if(!habitacion.get().getImagenesHabitacion().isEmpty()) {
                String habitacionImagen = habitacion.get().getImagenesHabitacion().stream().findFirst().get().getUrl();
                modelo.addAttribute("imagenHabitacion", habitacionImagen);
            }

            modelo.addAttribute("recomendados", servicioHabitacion.obtenerHotelesEnTuZona(habitacion.get()));

            return "habitaciones/detallesHabitacion";
        } else {
            // Hotel no encontrado - htlm
            return "habitacionNoEncontrado";
        }


    }

}

