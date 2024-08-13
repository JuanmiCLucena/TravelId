package com.eoi.grupo5.controladores;

import com.eoi.grupo5.dtos.UsuarioRegistroDto;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.servicios.ServicioDetallesUsuario;
import com.eoi.grupo5.servicios.ServicioUsuario;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final ServicioUsuario servicioUsuario;
    private final ServicioDetallesUsuario servicioDetallesUsuario;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PerfilController(ServicioUsuario servicioUsuario, ServicioDetallesUsuario servicioDetallesUsuario, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.servicioUsuario = servicioUsuario;
        this.servicioDetallesUsuario = servicioDetallesUsuario;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // Mostrar la página de perfil
    @GetMapping
    public String mostrarPerfil(Model modelo) {
        String nombreUsuario = obtenerUsuarioAutenticado();
        Optional<Usuario> usuarioOpt = servicioUsuario.encuentraPorNombreUsuario(nombreUsuario);

        if (usuarioOpt.isPresent()) {
            UsuarioRegistroDto usuarioRegistroDto = UsuarioRegistroDto.from(usuarioOpt.get());
            modelo.addAttribute("usuario", usuarioRegistroDto);
            return "usuario/perfilUsuario";  // Asumiendo que esta es la vista del perfil de usuario
        } else {
            modelo.addAttribute("error", "Usuario no encontrado");
            return "redirect:/";  // Redirige a una página adecuada si no se encuentra el usuario
        }
    }

    // Método para procesar la actualización del perfil
    @PostMapping("/actualizar")
    public String actualizarPerfil(@Valid @ModelAttribute("usuario") UsuarioRegistroDto usuarioRegistroDto,
                                   BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            return "usuario/perfilUsuario";  // Vuelve a mostrar la vista del perfil si hay errores
        }

        String nombreUsuario = obtenerUsuarioAutenticado();
        Optional<Usuario> usuarioOpt = servicioUsuario.encuentraPorNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuarioExistente = usuarioOpt.get();
            usuarioRegistroDto.sanitize();
            Usuario usuarioActualizado = Usuario.from(usuarioRegistroDto);
            usuarioActualizado.setId(usuarioExistente.getId());
            usuarioActualizado.getDetalles().setId(usuarioExistente.getDetalles().getId());
            usuarioActualizado.setPassword(usuarioExistente.getPassword());  // Mantenemos la contraseña existente
            servicioUsuario.guardar(usuarioActualizado);
            modelo.addAttribute("success", "¡Tu perfil ha sido actualizado con éxito!");
            return "redirect:/perfil?success";
        } else {
            modelo.addAttribute("error", "Error al actualizar el perfil. Usuario no encontrado.");
            return "usuario/perfilUsuario";
        }
    }

    // Método auxiliar para obtener el nombre del usuario autenticado
    private String obtenerUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
