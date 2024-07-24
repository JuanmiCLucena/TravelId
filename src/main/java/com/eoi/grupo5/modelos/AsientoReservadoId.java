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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AsientoReservadoId implements Serializable {

    @Column(name = "idAsiento")
    private Integer idAsiento;

    @Column(name = "idReserva")
    private Integer idReserva;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsientoReservadoId that = (AsientoReservadoId) o;
        return Objects.equals(idAsiento, that.idAsiento) && Objects.equals(idReserva, that.idReserva);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAsiento, idReserva);
    }
}
