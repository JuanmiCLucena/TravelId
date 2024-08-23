package com.eoi.grupo5.controladores;

import com.eoi.grupo5.dtos.PasswordChangeDto;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PerfilController(ServicioUsuario servicioUsuario, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.servicioUsuario = servicioUsuario;
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
            modelo.addAttribute("passwordChangeDto", new PasswordChangeDto());
            return "usuario/perfilUsuario";
        } else {
            modelo.addAttribute("error", "Usuario no encontrado");
            return "redirect:/";
        }
    }

    // Método para procesar la actualización del perfil
    @PostMapping("/actualizar")
    public String actualizarPerfil(@Valid @ModelAttribute("usuario") UsuarioRegistroDto usuarioRegistroDto,
                                   @ModelAttribute("passwordChangeDto") PasswordChangeDto passwordChangeDto,
                                   BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            return "usuario/perfilUsuario";  // Vuelve a mostrar la vista del perfil si hay errores
        }

        String nombreUsuario = obtenerUsuarioAutenticado();
        Optional<Usuario> usuarioOpt = servicioUsuario.encuentraPorNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuarioExistente = usuarioOpt.get();
            // Si el campo de contraseña está vacío, mantenemos la contraseña actual
            if (usuarioRegistroDto.getPassword() == null || usuarioRegistroDto.getPassword().isEmpty()) {
                usuarioRegistroDto.setPassword(usuarioExistente.getPassword());
            }
            usuarioRegistroDto.sanitize();
            Usuario usuarioActualizado = Usuario.from(usuarioRegistroDto);
            usuarioActualizado.setId(usuarioExistente.getId());
            usuarioActualizado.getDetalles().setId(usuarioExistente.getDetalles().getId());

            servicioUsuario.guardar(usuarioActualizado);
            modelo.addAttribute("success", "¡Tu perfil ha sido actualizado con éxito!");
            return "redirect:/perfil?success";
        } else {
            modelo.addAttribute("error", "Error al actualizar el perfil. Usuario no encontrado.");
            return "usuario/perfilUsuario";
        }
    }

    // Método para cambiar la contraseña
    @PostMapping("/cambiar-password")
    public String cambiarPassword(@Valid @ModelAttribute("passwordChangeDto") PasswordChangeDto passwordChangeDto,
                                  BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            return "usuario/perfilUsuario";  // Vuelve a la vista del perfil si hay errores
        }

        String nombreUsuario = obtenerUsuarioAutenticado();
        Optional<Usuario> usuarioOpt = servicioUsuario.encuentraPorNombreUsuario(nombreUsuario);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Verificar si la contraseña actual es correcta
            if (!bCryptPasswordEncoder.matches(passwordChangeDto.getPasswordActual(), usuario.getPassword())) {
                modelo.addAttribute("error", "La contraseña actual es incorrecta.");
                return "usuario/perfilUsuario";  // Vuelve a la vista del perfil
            }

            // Verificar si las nuevas contraseñas coinciden
            if (!passwordChangeDto.getNuevaPassword().equals(passwordChangeDto.getConfirmarPassword())) {
                modelo.addAttribute("error", "La nueva contraseña y la confirmación no coinciden.");
                return "usuario/perfilUsuario";  // Vuelve a la vista del perfil
            }

            // Actualizar la contraseña
            usuario.setPassword(bCryptPasswordEncoder.encode(passwordChangeDto.getNuevaPassword()));
            servicioUsuario.guardar(usuario);

            modelo.addAttribute("success", "¡Tu contraseña ha sido cambiada con éxito!");
            return "redirect:/perfil?success";
        } else {
            modelo.addAttribute("error", "Usuario no encontrado.");
            return "redirect:/";  // Redirige a la página principal o de login
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


