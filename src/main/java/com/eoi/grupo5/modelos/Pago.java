package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Representa un pago en el sistema.
 * Incluye detalles sobre el importe del pago, la fecha de pago,
 * la reserva asociada y el método de pago utilizado.
 */
@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "importe")
    @NotNull(message = "El importe no puede ser nulo")
    @Min(value = 0, message = "El importe debe ser mayor o igual a 0")
    private Double importe;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fechaPago")
    @NotNull(message = "Debes introducir una fecha de pago")
    private LocalDateTime fechaPago;

    /**
     * Relación One-to-One con la entidad {@link Reserva}.
     * Cada pago está asociado a una única reserva.
     */
    @OneToOne(mappedBy = "pago", cascade = CascadeType.ALL)
    private Reserva reserva;

    /**
     * Relación Many-to-One con la entidad {@link MetodoPago}.
     * Cada pago utiliza un método de pago específico.
     */
    @ManyToOne
    @JoinColumn(name = "idMetodoPago", foreignKey = @ForeignKey(name = "fkPagosMetodos"), nullable = false)
    @NotNull(message = "Debes introducir un método de pago")
    private MetodoPago metodoPago;
}
