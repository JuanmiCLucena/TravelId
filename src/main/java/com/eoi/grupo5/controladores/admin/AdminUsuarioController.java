package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.dtos.UsuarioRegistroDto;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.paginacion.PaginaRespuestaUsuarios;
import com.eoi.grupo5.servicios.ServicioDetallesUsuario;
import com.eoi.grupo5.servicios.ServicioUsuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final ServicioUsuario servicioUsuario;
    private final ServicioDetallesUsuario servicioDetallesUsuario;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminUsuarioController(ServicioUsuario servicioUsuario, ServicioDetallesUsuario servicioDetallesUsuario, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.servicioUsuario = servicioUsuario;
        this.servicioDetallesUsuario = servicioDetallesUsuario;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaUsuarios<Usuario> usuariosPage = servicioUsuario.buscarEntidadesPaginadas(page, size);
        List<Usuario> usuarios = usuariosPage.getContent();
        modelo.addAttribute("usuarios", usuarios);
        modelo.addAttribute("page", usuariosPage);
        return "admin/usuarios/adminUsuarios";
    }

    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        UsuarioRegistroDto usuario = new UsuarioRegistroDto();
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
        return "admin/usuarios/adminNuevoUsuario";
    }

    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("usuario") UsuarioRegistroDto usuarioRegistroDto, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
            return "admin/usuarios/adminNuevoUsuario";
        }

        try {
//            usuarioRegistroDto.sanitize();
            Usuario usuario = Usuario.from(usuarioRegistroDto);
            usuario.getDetalles().setUsu(usuario);
            usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));
            servicioUsuario.guardar(usuario);
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al crear el usuario: " + e.getMessage());
            return "admin/usuarios/adminNuevoUsuario";
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(Model modelo, @PathVariable Integer id) {
        Optional<Usuario> usuario = servicioUsuario.encuentraPorId(id);
        if (usuario.isPresent()) {
            UsuarioRegistroDto usuarioRegistroDto = UsuarioRegistroDto.from(usuario.get());
            modelo.addAttribute("usuario", usuarioRegistroDto);
            modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
            return "admin/usuarios/adminDetallesUsuario";
        } else {
            modelo.addAttribute("error", "Usuario no encontrado");
            return "admin/usuarios/adminUsuarios";
        }
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, @Valid @ModelAttribute("usuario") UsuarioRegistroDto usuarioRegistroDto, BindingResult result, Model modelo) {

//        usuarioRegistroDto.sanitize();

        // Verifica si hay errores en el BindingResult actualizado
        if (result.hasErrors()) {
            modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
            return "admin/usuarios/adminDetallesUsuario";
        }


        try {

            Optional<Usuario> optionalUsuario = servicioUsuario.encuentraPorId(id);
            if (optionalUsuario.isPresent()) {
                Usuario usuarioExistente = optionalUsuario.get();
                Usuario usuarioActualizado = Usuario.from(usuarioRegistroDto);
                usuarioActualizado.setId(usuarioExistente.getId());
                usuarioActualizado.setPassword(bCryptPasswordEncoder.encode(usuarioRegistroDto.getPassword()));
                usuarioActualizado.getDetalles().setId(usuarioExistente.getDetalles().getId());
                servicioUsuario.guardar(usuarioActualizado);
            } else {
                modelo.addAttribute("error", "Usuario no encontrado");
                return "admin/usuarios/adminUsuarios";
            }
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al editar el usuario: " + e.getMessage());
            return "admin/usuarios/adminDetallesUsuario";
        }
        return "redirect:/admin/usuarios";
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Usuario> optionalUsuario = servicioUsuario.encuentraPorId(id);
        if (optionalUsuario.isPresent()) {
            servicioUsuario.eliminarPorId(id);
        }
        return "redirect:/admin/usuarios";
    }

}
