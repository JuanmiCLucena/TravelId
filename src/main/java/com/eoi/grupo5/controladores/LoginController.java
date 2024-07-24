package com.eoi.grupo5.controladores;

import com.eoi.grupo5.modelos.DetallesUsuario;
import com.eoi.grupo5.modelos.Usuario;
import com.eoi.grupo5.repos.RepoDetallesUsuario;
import com.eoi.grupo5.repos.RepoUsuario;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    RepoUsuario repoUsuario;
    RepoDetallesUsuario repoDetallesUsuario;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginController(RepoUsuario repoUsuario, BCryptPasswordEncoder bCryptPasswordEncoder, RepoDetallesUsuario detallesUsuario){
        this.repoUsuario = repoUsuario;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.repoDetallesUsuario = detallesUsuario;
    }

    @GetMapping("/login")
    String Login(){
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid @RequestParam String username, @RequestParam String password, Model model){
        Optional<Usuario> optionalUsuario = repoUsuario.findByNombreUsuario(username);
        if (optionalUsuario.isPresent() && optionalUsuario.get().getPassword().equals(bCryptPasswordEncoder.encode(password))){
            Usuario usuario = optionalUsuario.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("msg", "Usuario encontrado");
            return "redirect:/";
        } else {
            model.addAttribute("msg", "Usuario no encontrado");
            return "redirect:/login?error=true";
        }

    }

    @GetMapping("/register")
    String Register(Model modelo) {
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String email,@RequestParam String username, @RequestParam String password, @RequestParam String repeatPassword , Model model){

        if(password.equals(repeatPassword)) {
            DetallesUsuario details = new DetallesUsuario(email);

            Usuario newUser = new Usuario(username, bCryptPasswordEncoder.encode(password), details);
            details.setUsu(newUser);
            repoDetallesUsuario.save(details);
            repoUsuario.save(newUser);

            return "index";
        } else {
            model.addAttribute("msg", "Las contraseñas no coinciden");
        }

        return "redirect:/register?error=true";
    }

}
