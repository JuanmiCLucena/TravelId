package com.eoi.grupo5.controladores;

import com.eoi.grupo5.email.CustomEmailService;
import com.eoi.grupo5.modelos.DetallesUsuario;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoDetallesUsuario;
import com.eoi.grupo5.repos.RepoUsuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    String Login(){
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
        return "loginForm/register";

    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String email,@RequestParam String username, @RequestParam String password, @RequestParam String repeatPassword , Model model){

        if(password.equals(repeatPassword)) {
            DetallesUsuario details = new DetallesUsuario(email);

            Usuario newUser = new Usuario(username, bCryptPasswordEncoder.encode(password), details);
            details.setUsu(newUser);
            repoDetallesUsuario.save(details);
            repoUsuario.save(newUser);
            emailService.sendSimpleMessage(details.getEmail(), "Bienvenido a Travel ID " + details.getNombre(), "Bienvenido a Travel ID" + details.getNombre());

            return "index";
        } else {
            model.addAttribute("msg", "Las contraseñas no coinciden");
        }

        return "redirect:/register?error=true";
    }

}
