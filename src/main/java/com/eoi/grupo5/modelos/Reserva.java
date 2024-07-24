package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaInicio")
    @NotNull(message = "Debes introducir una fecha de inicio de reserva")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaFin")
    @NotNull(message = "Debes introducir una fecha de fin de reserva")
    private LocalDateTime fechaFin;

    @Basic(optional = false)
    private boolean cancelado = false;

    @ManyToOne
    @JoinColumn(name = "idUsuario", foreignKey = @ForeignKey(name = "fkReservasUsuarios"), nullable = false)
    @NotNull(message = "Debe haber un usuario asociado a la reserva")
    private Usuario usu;


    @OneToMany(mappedBy = "reserva")
    private Set<AsientoReservado> asientosReservados = new HashSet<>();

    @OneToMany(mappedBy = "reserva")
    private Set<HabitacionReservada> habitacionesReservadas = new HashSet<>();

    @ManyToMany
    @JoinTable(
    name = "actividadesReservadas",
    joinColumns = @JoinColumn(name = "idReserva", foreignKey = @ForeignKey(name = "fkActresReservas")),
    inverseJoinColumns = @JoinColumn(name = "idActividad", foreignKey = @ForeignKey(name = "fkActresActividades")))
    private Set<Actividad> actividades = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "idPago", referencedColumnName = "id")
    private Pago pago;

}
