package com.eoi.grupo5.controladores;

import com.eoi.grupo5.dtos.UsuarioDto;
import com.eoi.grupo5.email.CustomEmailService;
import com.eoi.grupo5.modelos.DetallesUsuario;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoDetallesUsuario;
import com.eoi.grupo5.repos.RepoUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * El controlador `LoginController` gestiona las operaciones relacionadas con el inicio de sesión y registro de usuarios en la aplicación.
 * Proporciona las vistas para el inicio de sesión y el registro de nuevos usuarios, así como la lógica para procesar estas acciones.
 *
 * Funcionalidades principales:
 * - **Inicio de Sesión**: Muestra el formulario de inicio de sesión y maneja los mensajes de error relacionados.
 * - **Registro de Usuario**: Muestra el formulario de registro y maneja la creación de nuevos usuarios,
 * incluyendo la validación de datos y el envío de un correo electrónico de bienvenida.
 *
 * Dependencias:
 * - {@link RepoUsuario}: Repositorio para acceder y gestionar los datos de los usuarios.
 * - {@link RepoDetallesUsuario}: Repositorio para gestionar los detalles adicionales de los usuarios.
 * - {@link BCryptPasswordEncoder}: Utilizado para encriptar contraseñas de usuario.
 * - {@link CustomEmailService}: Servicio para el envío de correos electrónicos.
 *
 * @see RepoUsuario
 * @see RepoDetallesUsuario
 * @see BCryptPasswordEncoder
 * @see CustomEmailService
 */
@Controller
public class LoginController {

    private final RepoUsuario repoUsuario;
    private final RepoDetallesUsuario repoDetallesUsuario;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomEmailService emailService;

    /**
     * Constructor para inyectar las dependencias necesarias.
     *
     * @param repoUsuario Repositorio para gestionar usuarios.
     * @param bCryptPasswordEncoder Utilidad para encriptar contraseñas.
     * @param detallesUsuario Repositorio para gestionar detalles de usuarios.
     * @param emailService Servicio para enviar correos electrónicos.
     */
    public LoginController(RepoUsuario repoUsuario, BCryptPasswordEncoder bCryptPasswordEncoder, RepoDetallesUsuario detallesUsuario, CustomEmailService emailService){
        this.repoUsuario = repoUsuario;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.repoDetallesUsuario = detallesUsuario;
        this.emailService = emailService;
    }

    /**
     * Muestra el formulario de inicio de sesión. Recupera y muestra cualquier mensaje de error almacenado en la sesión.
     *
     * @param model El modelo para añadir atributos a la vista.
     * @param request La solicitud HTTP para acceder a los atributos de sesión.
     * @return La vista del formulario de inicio de sesión.
     */
    @GetMapping("/login")
    public String Login(Model model, HttpServletRequest request) {
        // Recupera el mensaje de error desde la sesión (Es un objeto por eso lo casteamos de nuevo a String)
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if (errorMessage != null) {
            // Añade el mensaje de error al modelo
            model.addAttribute("errorMessage", errorMessage);
            // Elimina el mensaje de error de la sesión después de haberlo recuperado
            request.getSession().removeAttribute("errorMessage");
        }
        return "loginForm/login";
    }

//    @PostMapping("/login")
//    public String processLogin(@Valid @RequestParam String username, @RequestParam String password, Model model){
//        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(username);
//        if (optionalUsuario.isPresent() && optionalUsuario.get().getPassword().equals(bCryptPasswordEncoder.encode(password))){
//            Usuario usuario = optionalUsuario.get();
//            model.addAttribute("usuario", usuario);
//            model.addAttribute("msg", "Usuario encontrado");
//            return "redirect:/";
//        } else {
//            model.addAttribute("msg", "Usuario no encontrado");
//            return "redirect:/login?error=true";
//        }
//
//    }

    /**
     * Muestra el formulario de registro para nuevos usuarios.
     *
     * @param modelo El modelo para añadir atributos a la vista.
     * @return La vista del formulario de registro.
     */
    @GetMapping("/register")
    String Register(Model modelo) {
        modelo.addAttribute("usuarioDto", new UsuarioDto());
        return "loginForm/register";

    }

    /**
     * Procesa el registro de un nuevo usuario. Valida los datos proporcionados, crea el usuario y envía un correo electrónico de bienvenida.
     *
     * @param usuarioDto Datos del nuevo usuario proporcionados en el formulario.
     * @param bindingResult Resultado de la validación del formulario.
     * @param model El modelo para añadir atributos a la vista.
     * @return La vista de éxito si el registro es exitoso, o el formulario de registro con errores si falla.
     */
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute UsuarioDto usuarioDto, BindingResult bindingResult, Model model){

        // Validar el formulario
        if(bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "loginForm/register";
        }

        // Validar que las contraseñas coincidan
        if (!usuarioDto.getPassword().equals(usuarioDto.getRepeatPassword())) {
            bindingResult.rejectValue("repeatPassword", "error.usuarioDto", "Las contraseñas no coinciden");
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "loginForm/register";
        }

        // Validar que el nombre de usuario no esté en uso
        if (repoUsuario.existsBynombreUsuario(usuarioDto.getUsername())) {
            bindingResult.rejectValue("username", "error.usuarioDto", "El nombre de usuario ya está en uso");
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "loginForm/register";
        }

        // Validar que el correo electrónico no esté en uso
        if (repoDetallesUsuario.existsByEmail(usuarioDto.getEmail())) {
            bindingResult.rejectValue("email", "error.usuarioDto", "El correo electrónico ya está en uso");
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "loginForm/register";
        }

        // Crear el nuevo usuario y guardar en la base de datos
        DetallesUsuario details = new DetallesUsuario(usuarioDto.getEmail());
        Usuario newUser = new Usuario(usuarioDto.getUsername(), bCryptPasswordEncoder.encode(usuarioDto.getPassword()), details);
        details.setUsu(newUser);
        repoDetallesUsuario.save(details);
        repoUsuario.save(newUser);

        // Enviar correo electrónico de bienvenida
        emailService.sendSimpleMessage(details.getEmail(), "Bienvenido a TravelId " + newUser.getNombreUsuario(), "Bienvenido a TravelId " + newUser.getNombreUsuario());

        return "index";
    }


}
