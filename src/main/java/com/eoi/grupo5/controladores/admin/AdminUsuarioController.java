package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaUsuarios;
import com.eoi.grupo5.servicios.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final ServicioUsuario servicioUsuario;
    private final ServicioDetallesUsuario servicioDetallesUsuario;


    public AdminUsuarioController(ServicioUsuario servicioUsuario, ServicioDetallesUsuario servicioDetallesUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.servicioDetallesUsuario = servicioDetallesUsuario;
    }

    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaUsuarios<Usuario> usuariosPage = servicioUsuario.buscarEntidadesPaginadas(page, size);
        List<Usuario> usuarios = usuariosPage.getContent();
        modelo.addAttribute("usuarios",usuarios);
        modelo.addAttribute("page", usuariosPage);
        return "admin/usuarios/adminUsuarios";
    }

    @GetMapping("/{id}")
    public String detalles(Model modelo, @PathVariable Integer id) {
        Optional<Usuario> usuario = servicioUsuario.encuentraPorId(id);
        if (usuario.isPresent()) {
            modelo.addAttribute("usuario", usuario.get());
            modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
            return "admin/usuarios/adminDetallesUsuario";
        } else {
            modelo.addAttribute("error", "Usuario no encontrado");
            return "admin/adminUsuarios";
        }
    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        Usuario usuario = new Usuario();
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
        return "admin/usuarios/adminNuevoUsuario";
    }

    @PostMapping("/crear")
    public String crear(
            @ModelAttribute("usuario") Usuario usuario,
            @RequestParam("detalles.id") Integer detallesUsuarioId
    ) {

        try {

            DetallesUsuario detallesUsuario = servicioDetallesUsuario.encuentraPorId(detallesUsuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Detalles de Usuario no encontrados"));
            usuario.setDetalles(detallesUsuario);

            servicioUsuario.guardar(usuario);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/usuarios";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Usuario> optionalUsuario = servicioUsuario.encuentraPorId(id);
        if(optionalUsuario.isPresent()) {
            servicioUsuario.eliminarPorId(id);
        }
        return "redirect:/admin/usuarios";
    }

}