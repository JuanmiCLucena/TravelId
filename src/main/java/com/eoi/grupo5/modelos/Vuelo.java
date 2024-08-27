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

/**
 * Entidad que representa un vuelo en el sistema. Un vuelo está asociado a una compañía aérea,
 * tiene un origen, un destino, y puede contener múltiples asientos.
 */
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

    /**
     * Relación muchos a uno con la entidad {@link CompaniaVuelo}.
     * Un vuelo está asociado a una compañía aérea específica.
     */
    @ManyToOne
    @JoinColumn(name = "idCompania", foreignKey = @ForeignKey(name = "fkVuelosCompanias"), nullable = false)
    private CompaniaVuelo compania;

    /**
     * Relación muchos a uno con la entidad {@link Localizacion}, que representa el origen del vuelo.
     * El vuelo debe tener una ubicación de origen específica.
     */
    @ManyToOne
    @JoinColumn(name = "idOrigen", foreignKey = @ForeignKey(name = "fkVuelosOrigen"), nullable = false)
    private Localizacion origen;

    /**
     * Relación muchos a uno con la entidad {@link Localizacion}, que representa el destino del vuelo.
     * El vuelo debe tener una ubicación de destino específica.
     */
    @ManyToOne
    @JoinColumn(name = "idDestino", foreignKey = @ForeignKey(name = "fkVuelosDestino"), nullable = false)
    private Localizacion destino;

    /**
     * Relación uno a muchos con la entidad {@link Asiento}.
     * Un vuelo puede tener múltiples asientos disponibles para los pasajeros.
     */
    @OneToMany(mappedBy = "vuelo")
    private Set<Asiento> asientos = new HashSet<>();

    /**
     * Relación uno a uno con la entidad {@link Imagen}.
     * Un vuelo puede tener asociada una imagen específica, por ejemplo, para su presentación en una interfaz de usuario.
     */
    @OneToOne
    @JoinColumn(name = "idImagen")
    private Imagen imagen;

    /**
     * Helper para crear una instancia de {@link Vuelo} a partir de un {@link VueloFormDto}.
     * Este método permite mapear los datos de un DTO a la entidad de vuelo.
     *
     * @param vueloFormDto DTO con los datos del vuelo.
     * @return Instancia de {@link Vuelo} con los datos mapeados desde el DTO.
     */
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
