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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final RepoUsuario repoUsuario;
    private final RepoDetallesUsuario repoDetallesUsuario;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomEmailService emailService;

    public LoginController(RepoUsuario repoUsuario, BCryptPasswordEncoder bCryptPasswordEncoder, RepoDetallesUsuario detallesUsuario, CustomEmailService emailService){
        this.repoUsuario = repoUsuario;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.repoDetallesUsuario = detallesUsuario;
        this.emailService = emailService;
    }

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

    @GetMapping("/register")
    String Register(Model modelo) {
        modelo.addAttribute("usuarioDto", new UsuarioDto());
        return "loginForm/register";

    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute UsuarioDto usuarioDto, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "loginForm/register";
        }

        if (!usuarioDto.getPassword().equals(usuarioDto.getRepeatPassword())) {
            bindingResult.rejectValue("repeatPassword", "error.usuarioDto", "Las contraseñas no coinciden");
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "loginForm/register";
        }

        if (repoUsuario.existsBynombreUsuario(usuarioDto.getUsername())) {
            bindingResult.rejectValue("username", "error.usuarioDto", "El nombre de usuario ya está en uso");
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "loginForm/register";
        }

        if (repoDetallesUsuario.existsByEmail(usuarioDto.getEmail())) {
            bindingResult.rejectValue("email", "error.usuarioDto", "El correo electrónico ya está en uso");
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "loginForm/register";
        }

        DetallesUsuario details = new DetallesUsuario(usuarioDto.getEmail());
        Usuario newUser = new Usuario(usuarioDto.getUsername(), bCryptPasswordEncoder.encode(usuarioDto.getPassword()), details);
        details.setUsu(newUser);
        repoDetallesUsuario.save(details);
        repoUsuario.save(newUser);
        emailService.sendSimpleMessage(details.getEmail(), "Bienvenido a TravelId " + newUser.getNombreUsuario(), "Bienvenido a TravelId " + newUser.getNombreUsuario());

        return "index";
    }


}
