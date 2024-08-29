package com.eoi.grupo5.dtos;

import com.eoi.grupo5.modelos.Actividad;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * DTO para gestionar la creación y actualización de actividades en la parte de administración.
 *
 * <p>Este DTO está diseñado específicamente para la administración de actividades,
 * proporcionando campos adicionales para manejar operaciones CRUD desde la interfaz administrativa.</p>
 *
 * <p>A diferencia del {@link ActividadDto} que se utiliza en la parte de usuario,
 * el {@code ActividadFormDto} incluye validaciones más estrictas y soporte para
 * manejo de archivos de imagen, necesarios en la gestión administrativa de las actividades.</p>
 */
@Getter
@Setter
public class ActividadFormDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull(message = "El nombre es obligatorio")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @NotNull(message = "La descripción es obligatoria")
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaInicio")
    @NotNull(message = "Debes introducir una fecha de inicio")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaFin")
    @NotNull(message = "Debes introducir una fecha de fin")
    @Future(message = "La fecha de fin debe ser posterior a la fecha de Inicio")
    private LocalDateTime fechaFin;

    @NotNull(message = "La localización es obligatoria")
    private Integer localizacionId;

    @NotNull(message = "El tipo es obligatorio")
    private Integer tipoId;

    @NotNull(message = "El número máximo de asistentes es obligatorio")
    private Integer maximosAsistentes;

    @NotNull(message = "El número de asistentes confirmados es obligatorio")
    private Integer asistentesConfirmados;

    private MultipartFile[] imagenes; // Para manejar múltiples archivos de imagen

    /**
     * Helper para convertir una entidad {@link Actividad} en un DTO {@code ActividadFormDto}.
     *
     * <p>Este método facilita la conversión desde la entidad a su forma editable en la administración,
     * mapeando todos los campos necesarios para la gestión de la actividad.</p>
     *
     * @param actividad la entidad {@link Actividad} a convertir.
     * @return un objeto {@code ActividadFormDto} con los datos de la entidad.
     */
    public static ActividadFormDto from(Actividad actividad) {
        // Crear el DTO y mapear los valores de la entidad Actividad
        ActividadFormDto actividadFormDto = new ActividadFormDto();
        actividadFormDto.setId(actividad.getId());
        actividadFormDto.setNombre(actividad.getNombre());
        actividadFormDto.setDescripcion(actividad.getDescripcion());
        actividadFormDto.setFechaInicio(actividad.getFechaInicio());
        actividadFormDto.setFechaFin(actividad.getFechaFin());
        actividadFormDto.setLocalizacionId(actividad.getLocalizacion().getId());
        actividadFormDto.setTipoId(actividad.getTipo().getId());
        actividadFormDto.setMaximosAsistentes(actividad.getMaximosAsistentes());
        actividadFormDto.setAsistentesConfirmados(actividad.getAsistentesConfirmados());

        return actividadFormDto;
    }

}
