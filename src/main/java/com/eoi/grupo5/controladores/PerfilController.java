package com.eoi.grupo5.controladores;

import com.eoi.grupo5.dtos.UsuarioRegistroDto;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.servicios.ServicioDetallesUsuario;
import com.eoi.grupo5.servicios.ServicioUsuario;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PerfilController {

    private final ServicioUsuario servicioUsuario;
    private final ServicioDetallesUsuario servicioDetallesUsuario;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PerfilController(ServicioUsuario servicioUsuario, ServicioDetallesUsuario servicioDetallesUsuario, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.servicioUsuario = servicioUsuario;
        this.servicioDetallesUsuario = servicioDetallesUsuario;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model modelo, @PathVariable Integer id) {
        // Aquí obtienes el usuario logueado.
        Optional<Usuario> usuario = servicioUsuario.encuentraPorId(id);
        if (usuario.isPresent()) {
            UsuarioRegistroDto usuarioRegistroDto = UsuarioRegistroDto.from(usuario.get());
            modelo.addAttribute("usuario", usuarioRegistroDto);
            modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
            return "perfilUsuario";
        } else {
            modelo.addAttribute("error", "Usuario incorrecto");
            return "perfilUsuarioº";
        }
    }

    // Método para procesar la actualización del perfil
    @PostMapping("/perfil/actualizar")
    public String editarPerfil(@Valid @ModelAttribute("usuario") UsuarioRegistroDto usuarioRegistroDto, BindingResult result, Model modelo) {

        // Verifica si hay errores en el BindingResult actualizado
        if (result.hasErrors()) {
            return "perfilUsuario";  // Volver al formulario con los mensajes de error
        }

        try {
            // Obtén el nombre de usuario del contexto de seguridad
            String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<Usuario> optionalUsuario = servicioUsuario.findByNombreUsuario(nombreUsuario);

            if (optionalUsuario.isPresent()) {
                Usuario usuarioExistente = optionalUsuario.get();

                // Saneamos los campos convirtiendo los que estén vacíos a null
                usuarioRegistroDto.sanitize();

                // Actualizamos el usuario existente con los datos del DTO
                Usuario usuarioActualizado = Usuario.from(usuarioRegistroDto);

                // Mantenemos el mismo ID y no cambiamos la contraseña si el campo de la contraseña está vacío
                usuarioActualizado.setId(usuarioExistente.getId());

                if (!usuarioRegistroDto.getPassword().isEmpty()) {
                    usuarioActualizado.setPassword(bCryptPasswordEncoder.encode(usuarioRegistroDto.getPassword()));
                } else {
                    usuarioActualizado.setPassword(usuarioExistente.getPassword());
                }

                // Guardamos el usuario actualizado en la base de datos
                servicioUsuario.guardar(usuarioActualizado);

            } else {
                modelo.addAttribute("error", "Usuario no encontrado");
                return "perfilUsuario";  // Mostrar el perfil con un mensaje de error
            }
        } catch (Exception e) {
            modelo.addAttribute("error", "Error al editar el perfil: " + e.getMessage());
            return "perfilUsuario";  // Mostrar el perfil con un mensaje de error
        }

        // Redirigir a la vista del perfil con un mensaje de éxito o volver a la misma página
        return "redirect:/perfilUsuario?success";
    }
}
