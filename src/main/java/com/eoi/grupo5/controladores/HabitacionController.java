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

/**
 * El controlador `HabitacionController` gestiona las solicitudes relacionadas con las habitaciones en la aplicación.
 * Su principal función es proporcionar detalles sobre una habitación específica, incluyendo información de precios y
 * imágenes, y también recomendar habitaciones similares en la misma zona.
 *
 * Funcionalidades principales:
 * - **Detalles de la habitación**: Permite a los usuarios ver detalles completos de una habitación específica, incluyendo
 *   su precio actual, imágenes y recomendaciones de otras habitaciones en la misma zona.
 *
 * Dependencias:
 * - {@link ServicioHabitacion}: Servicio que gestiona la lógica de negocio relacionada con las habitaciones, incluyendo
 *   la búsqueda de habitaciones por ID, la obtención del precio actual y la recomendación de habitaciones similares.
 *
 * @see ServicioHabitacion
 */
@Controller
public class HabitacionController {

    private final ServicioHabitacion servicioHabitacion;

    /**
     * Constructor del controlador `HabitacionController`.
     *
     * @param servicioHabitacion Servicio para manejar la lógica de negocio relacionada con las habitaciones.
     */
    public HabitacionController(ServicioHabitacion servicioHabitacion) {
        this.servicioHabitacion = servicioHabitacion;
    }

    /**
     * Muestra los detalles de una habitación específica.
     *
     * @param id El identificador de la habitación a mostrar.
     * @param modelo El objeto Model que se utiliza para pasar datos a la vista.
     * @return La vista que muestra los detalles de la habitación si se encuentra; de lo contrario, muestra una página de error.
     */
    @GetMapping("/habitacion/{id}")
    public String detallesHabitacion(@PathVariable Integer id, Model modelo) {
        Optional<Habitacion> habitacion = servicioHabitacion.encuentraPorId(id);

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
            // En caso de que la habitación no sea encontrada
            return "habitacionNoEncontrado";
        }
    }
}
