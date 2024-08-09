package com.eoi.grupo5.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class HotelFormDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotBlank(message = "El Hotel debe tener un nombre")
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;

    @NotNull(message = "El Hotel debe tener una categoria")
    @Min(value = 1, message = "La categoría debe ser al menos 1")
    @Max(value = 5, message = "La categoría no puede ser mayor a 5")
    private Byte categoria;

    @NotBlank(message = "El Hotel debe tener una descripción")
    @Size(max = 150, message = "La descripción no puede tener más de 150 caracteres")
    private String descripcion;

    @Size(max = 45, message = "El contacto no puede tener más de 45 caracteres")
    private String contacto;

    @NotNull(message = "El hotel debe tener una localización")
    private Integer localizacionId;

    private MultipartFile[] imagenes;
}
