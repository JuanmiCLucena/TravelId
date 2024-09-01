package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.dtos.UsuarioRegistroDto;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.paginacion.PaginaRespuestaUsuarios;
import com.eoi.grupo5.servicios.ServicioDetallesUsuario;
import com.eoi.grupo5.servicios.ServicioUsuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestionar las operaciones de administración relacionadas con los usuarios.
 * Permite crear, listar, editar y eliminar usuarios desde la interfaz de administración.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final ServicioUsuario servicioUsuario;
    private final ServicioDetallesUsuario servicioDetallesUsuario;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param servicioUsuario Servicio para manejar las operaciones de usuario.
     * @param servicioDetallesUsuario Servicio para manejar los detalles del usuario.
     * @param bCryptPasswordEncoder Codificador de contraseñas para asegurar las contraseñas de usuario.
     */
    public AdminUsuarioController(ServicioUsuario servicioUsuario, ServicioDetallesUsuario servicioDetallesUsuario, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.servicioUsuario = servicioUsuario;
        this.servicioDetallesUsuario = servicioDetallesUsuario;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    /**
     * Maneja la solicitud GET para listar todos los usuarios paginados.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param page Número de la página solicitada (por defecto 0).
     * @param size Tamaño de la página (por defecto 10).
     * @return El nombre de la plantilla que muestra la lista de usuarios.
     */
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

    /**
     * Muestra la página para crear un nuevo usuario.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @return El nombre de la plantilla que muestra el formulario de creación de usuario.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        UsuarioRegistroDto usuario = new UsuarioRegistroDto();
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
        return "admin/usuarios/adminNuevoUsuario";
    }

    /**
     * Maneja la solicitud POST para crear un nuevo usuario.
     *
     * @param usuarioRegistroDto Datos del nuevo usuario, validado.
     * @param result Resultado de la validación de los datos del usuario.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de usuarios o la vista de creación si hay errores.
     */
    @PostMapping("/crear")
    public String crear(@Valid @ModelAttribute("usuario") UsuarioRegistroDto usuarioRegistroDto, BindingResult result, Model modelo) {
        if (result.hasErrors()) {
            modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
            return "admin/usuarios/adminNuevoUsuario";
        }

        try {
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

    /**
     * Muestra la página para editar un usuario existente.
     *
     * @param modelo Modelo para pasar datos a la vista.
     * @param id ID del usuario que se va a editar.
     * @return El nombre de la plantilla que muestra el formulario de edición de usuario, o una vista de error si el usuario no se encuentra.
     */
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

    /**
     * Maneja la solicitud PUT para editar un usuario existente.
     *
     * @param id ID del usuario que se va a actualizar.
     * @param usuarioRegistroDto Datos del usuario actualizado, validado.
     * @param result Resultado de la validación de los datos del usuario.
     * @param modelo Modelo para pasar datos a la vista.
     * @return Redirección a la lista de usuarios o la vista de edición si hay errores.
     */
    @PutMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, @Valid @ModelAttribute("usuario") UsuarioRegistroDto usuarioRegistroDto, BindingResult result, Model modelo) {


        // Verifica si hay errores en el BindingResult actualizado
        if (result.hasErrors()) {
            modelo.addAttribute("detalles", servicioDetallesUsuario.buscarEntidades());
            return "admin/usuarios/adminDetallesUsuario";
        }


        try {
            Optional<Usuario> optionalUsuario = servicioUsuario.encuentraPorId(id);
            if (optionalUsuario.isPresent()) {
                Usuario usuarioExistente = optionalUsuario.get();
                // Saneamos los campos convirtiendo los que estén vacíos a null
                usuarioRegistroDto.sanitize();
                Usuario usuarioActualizado = Usuario.from(usuarioRegistroDto);
                usuarioActualizado.setId(usuarioExistente.getId());
                usuarioActualizado.getDetalles().setId(usuarioExistente.getDetalles().getId());
                usuarioActualizado.setPassword(bCryptPasswordEncoder.encode(usuarioRegistroDto.getPassword()));
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

    /**
     * Maneja la solicitud DELETE para eliminar un usuario existente.
     *
     * @param id ID del usuario que se va a eliminar.
     * @return Redirección a la lista de usuarios.
     */
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Usuario> optionalUsuario = servicioUsuario.encuentraPorId(id);
        if (optionalUsuario.isPresent()) {
            servicioUsuario.eliminarPorId(id);
        }
        return "redirect:/admin/usuarios";
    }

}
