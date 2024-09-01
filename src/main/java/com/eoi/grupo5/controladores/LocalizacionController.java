package com.eoi.grupo5.controladores;

import com.eoi.grupo5.servicios.ServicioLocalizacion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * El controlador `LocalizacionController` maneja las solicitudes relacionadas con la búsqueda y autocompletación de localizaciones.
 * Su principal función es proporcionar una lista de sugerencias para completar la búsqueda de localizaciones en función de una consulta proporcionada por el usuario.
 *
 * Funcionalidades principales:
 * - **Autocompletado de Localizaciones**: Ofrece sugerencias de localizaciones basadas en una cadena de consulta ingresada por el usuario.
 *
 * Dependencias:
 * - {@link ServicioLocalizacion}: Servicio que maneja la lógica de negocio y la consulta de localizaciones.
 *
 * @see ServicioLocalizacion
 */
@Controller
public class LocalizacionController {

    private final ServicioLocalizacion servicioLocalizacion;

    /**
     * Constructor para inyectar el servicio de localización.
     *
     * @param servicioLocalizacion Servicio que maneja la lógica de negocio para las localizaciones.
     */
    public LocalizacionController(ServicioLocalizacion servicioLocalizacion) {
        this.servicioLocalizacion = servicioLocalizacion;
    }

    /**
     * Maneja la solicitud GET para la autocompletación de localizaciones.
     * Proporciona una lista de localizaciones que contienen la cadena de consulta proporcionada.
     *
     * @param query Cadena de consulta para buscar localizaciones.
     * @return Una lista de nombres de localizaciones que coinciden con la consulta.
     */
    @GetMapping("/autocomplete-localizacion")
    @ResponseBody
    public List<String> autocompleteLocalizacion(@RequestParam("query") String query) {
        return servicioLocalizacion.findByNombreContaining(query);
    }
}
