package com.eoi.grupo5.modelos;

import com.eoi.grupo5.dtos.UsuarioRegistroDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombreUsuario", nullable = false, length = 45)
    @NotNull(message = "Debes introducir un nombre de usuario")
    @Size(min = 2, max = 45, message = "El nombre de usuario debe tener entre 2 y 45 caracteres")
    private String nombreUsuario;

    @Column(name = "password", nullable = false, length = 150)
    @NotNull(message = "La contraseña no puede ser nula")
    @Size(min = 5, max = 150, message = "La contraseña debe tener entre 5 y 150 caracteres")
    private String password;

    @OneToOne(mappedBy = "usu",cascade = CascadeType.ALL)
    private DetallesUsuario detalles;

    @OneToMany(mappedBy = "usu", fetch = FetchType.EAGER)
    private Set<Reserva> reservas = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rolesUsuario",
            joinColumns = @JoinColumn(name = "usuarioId"),
            inverseJoinColumns = @JoinColumn(name = "rolId")
    )
    private Set<Role> roles = new HashSet<>();

    @Basic(optional = false)
    private boolean active = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            GrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName().toUpperCase());
            authorities.add(authority);
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return nombreUsuario;
    }

    public Usuario(String nombreUsuario, String password, DetallesUsuario detalles) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.detalles = detalles;
    }

    // Helpers
    public static Usuario from(UsuarioRegistroDto usuarioRegistroDto) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioRegistroDto.getId());
        usuario.setNombreUsuario(usuarioRegistroDto.getNombreUsuario());
        usuario.setPassword(usuarioRegistroDto.getPassword());
        DetallesUsuario detallesUsuario = new DetallesUsuario();
        usuario.setDetalles(detallesUsuario);
        usuario.getDetalles().setEmail(usuarioRegistroDto.getEmail());
        usuario.getDetalles().setNombre(usuarioRegistroDto.getNombre());
        usuario.getDetalles().setApellidos(usuarioRegistroDto.getApellidos());
        usuario.getDetalles().setTelefono(usuarioRegistroDto.getTelefono());
        usuario.getDetalles().setEdad(usuarioRegistroDto.getEdad());
        usuario.getDetalles().setDni(usuarioRegistroDto.getDni());
        return usuario;
    }

}
