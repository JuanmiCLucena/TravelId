package com.eoi.grupo5.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase DTO que representa un asiento.
 * <p>
 * Esta clase se utiliza para transferir información sobre un asiento entre diferentes capas de la aplicación.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsientoDto {

    private Integer id;
    private String numero;
    private boolean reservado;
    private String categoria;
    private double precio;

}
