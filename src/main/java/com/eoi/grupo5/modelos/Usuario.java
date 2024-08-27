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

/**
 * Entidad que representa a un usuario en el sistema, implementando las interfaces necesarias
 * para la autenticación y autorización en Spring Security.
 * <p>
 * Un usuario posee credenciales de acceso, roles asociados y puede realizar reservas
 * de servicios. Además, contiene información adicional en la entidad {@link DetallesUsuario}.
 * </p>
 */
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

    /**
     * Relación uno a uno con {@link DetallesUsuario}.
     * Cada usuario tiene un conjunto de detalles adicionales como nombre, apellidos, y email.
     * La relación es bidireccional y utiliza un mapeo desde el lado de {@link DetallesUsuario}.
     */
    @OneToOne(mappedBy = "usu", cascade = CascadeType.ALL)
    private DetallesUsuario detalles;

    /**
     * Relación uno a muchos con la entidad {@link Reserva}.
     * Un usuario puede tener múltiples reservas, las cuales están asociadas de manera
     * directa a su cuenta. La carga es EAGER para asegurar que las reservas se cargan
     * junto con el usuario.
     */
    @OneToMany(mappedBy = "usu", fetch = FetchType.EAGER)
    private Set<Reserva> reservas = new HashSet<>();

    /**
     * Relación muchos a muchos con la entidad {@link Role}.
     * Define los roles de seguridad que un usuario tiene en el sistema.
     * Utiliza una tabla intermedia llamada `rolesUsuario` para gestionar la relación.
     * La carga es EAGER para asegurar que los roles se cargan junto con el usuario.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "rolesUsuario",
            joinColumns = @JoinColumn(name = "usuarioId"),
            inverseJoinColumns = @JoinColumn(name = "rolId")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Indica si el usuario está activo o no.
     * Este campo es utilizado por Spring Security para habilitar o deshabilitar el acceso del usuario.
     */
    @Basic(optional = false)
    private boolean active = true;

    /**
     * Devuelve las autoridades (roles) del usuario en el formato esperado por Spring Security.
     *
     * @return Colección de {@link GrantedAuthority} que representa los roles del usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            GrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName().toUpperCase());
            authorities.add(authority);
        }
        return authorities;
    }

    /**
     * Devuelve el nombre de usuario, requerido por Spring Security para la autenticación.
     *
     * @return Nombre de usuario.
     */
    @Override
    public String getUsername() {
        return nombreUsuario;
    }

    /**
     * Constructor que permite crear un usuario a partir de su nombre de usuario, contraseña y detalles adicionales.
     *
     * @param nombreUsuario Nombre del usuario.
     * @param password      Contraseña del usuario.
     * @param detalles      Detalles adicionales del usuario.
     */
    public Usuario(String nombreUsuario, String password, DetallesUsuario detalles) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.detalles = detalles;
    }

    /**
     * Crea un objeto {@link Usuario} a partir de un DTO de registro.
     * <p>
     * Este método es un helper que transforma un {@link UsuarioRegistroDto} en una entidad
     * de usuario lista para ser persistida en la base de datos.
     * </p>
     *
     * @param usuarioRegistroDto DTO con los datos del usuario a registrar.
     * @return Instancia de {@link Usuario} con los datos del DTO.
     */
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
