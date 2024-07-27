package com.eoi.grupo5.controladores.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AdminInicioController {

    @GetMapping("/admin/inicio")
    public String inicio(Principal principal, Model modelo) {
        modelo.addAttribute("username", principal.getName());
        return "admin/adminIndex";
    }



}
