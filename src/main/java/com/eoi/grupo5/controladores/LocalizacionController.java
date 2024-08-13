package com.eoi.grupo5.controladores;

import com.eoi.grupo5.servicios.ServicioLocalizacion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class LocalizacionController {

    private final ServicioLocalizacion servicioLocalizacion;

    public LocalizacionController(ServicioLocalizacion servicioLocalizacion) {
        this.servicioLocalizacion = servicioLocalizacion;
    }


    @GetMapping("/autocomplete-localizacion")
    @ResponseBody
    public List<String> autocompleteLocalizacion(@RequestParam("query") String query) {
        return servicioLocalizacion.findByNombreContaining(query);
    }
}
