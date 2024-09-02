package com.eoi.grupo5.controladores.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Controlador para gestionar la página de inicio de la sección de administración.
 * Este controlador maneja la solicitud GET para mostrar la página principal del administrador.
 */
@Controller
public class AdminInicioController {

    /**
     * Maneja la solicitud GET para mostrar la página de inicio de la administración.
     *
     * @param principal Objeto que contiene la información del usuario autenticado.
     * @param modelo    Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra la página de inicio de la administración.
     */
    @GetMapping("/admin/inicio")
    public String inicio(Principal principal, Model modelo) {
        modelo.addAttribute("username", principal.getName());
        return "admin/adminIndex";
    }



}
