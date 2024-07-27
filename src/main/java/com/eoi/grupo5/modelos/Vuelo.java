package com.eoi.grupo5.modelos;

import com.eoi.grupo5.dtos.VueloFormDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vuelos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 100, nullable = false)
    @NotNull(message = "El nombre del vuelo no puede ser nulo")
    private String nombre;

    @Column(name = "descripcion", length = 250)
    @Size(max = 250, message = "La descripción no puede tener más de 250 caracteres")
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaSalida")
    @NotNull(message = "La fecha de salida no puede ser nula")
    private LocalDateTime fechaSalida;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaLlegada")
    @NotNull(message = "La fecha de llegada no puede ser nula")
    private LocalDateTime fechaLlegada;

    @ManyToOne
    @JoinColumn(name = "idCompania", foreignKey = @ForeignKey(name = "fkVuelosCompanias"), nullable = false)
    private CompaniaVuelo compania;

    @ManyToOne
    @JoinColumn(name = "idOrigen", foreignKey = @ForeignKey(name = "fkVuelosOrigen"), nullable = false)
    private Localizacion origen;

    @ManyToOne
    @JoinColumn(name = "idDestino", foreignKey = @ForeignKey(name = "fkVuelosDestino"), nullable = false)
    private Localizacion destino;

    @OneToMany(mappedBy = "vuelo")
    private Set<Asiento> asientos = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "idImagen")
    private Imagen imagen;

    // Helpers
    public static Vuelo from(VueloFormDto vueloFormDto) {
        Vuelo vuelo = new Vuelo();
        vuelo.setId(vueloFormDto.getId());
        vuelo.setNombre(vueloFormDto.getNombre());
        vuelo.setCompania(vueloFormDto.getCompania());
        vuelo.setDescripcion(vueloFormDto.getDescripcion());
        vuelo.setFechaSalida(vueloFormDto.getFechaSalida());
        vuelo.setFechaLlegada(vueloFormDto.getFechaLlegada());
        vuelo.setDestino(vueloFormDto.getDestino());
        vuelo.setOrigen(vueloFormDto.getOrigen());
        vuelo.setAsientos(vueloFormDto.getAsientos());
        return vuelo;
    }

}
