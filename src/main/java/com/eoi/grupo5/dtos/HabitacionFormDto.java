package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.Habitacion;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class HabitacionFormDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull(message = "El número de habitación no puede ser nulo")
    @Min(value = 1, message = "El número de habitación debe ser un valor positivo")
    private Integer numero;

    @NotNull(message = "La capacidad de habitación no puede ser nula")
    @Min(value = 1, message = "La capacidad de habitación debe ser al menos 1")
    private Byte capacidad;

    @NotNull(message = "El número de camas no puede ser nulo")
    @Min(value = 1, message = "El número de camas debe ser al menos 1")
    private Byte numeroCamas;

    @NotNull(message = "El hotel debe ser seleccionado")
    private Integer hotelId;

    @NotNull(message = "El tipo de habitación debe ser seleccionado")
    private Integer tipoId;

    @Column(name = "descripcion", length = 150)
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    private String descripcion;

    private MultipartFile[] imagenes;

    public static HabitacionFormDto from(Habitacion habitacion) {
        HabitacionFormDto habitacionFormDto = new HabitacionFormDto();
        habitacionFormDto.setId(habitacion.getId());
        habitacionFormDto.setNumero(habitacion.getNumero());
        habitacionFormDto.setCapacidad(habitacion.getCapacidad());
        habitacionFormDto.setNumeroCamas(habitacion.getNumeroCamas());
        habitacionFormDto.setHotelId(habitacion.getHotel().getId());
        habitacionFormDto.setTipoId(habitacion.getTipo().getId());
        habitacionFormDto.setDescripcion(habitacion.getTipo().getDescripcion());

        return habitacionFormDto;
    }

}
