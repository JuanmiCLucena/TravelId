package com.eoi.grupo5.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionReservadaId implements Serializable{

    @Column(name = "idHabitacion")
    private Integer idHabitacion;

    @Column(name = "idReserva")
    private Integer idReserva;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HabitacionReservadaId that = (HabitacionReservadaId) o;
        return Objects.equals(idHabitacion, that.idHabitacion) && Objects.equals(idReserva, that.idReserva);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idHabitacion, idReserva);
    }
}



