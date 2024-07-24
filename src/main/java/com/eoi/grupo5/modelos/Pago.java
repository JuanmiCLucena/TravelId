package com.eoi.grupo5.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(name = "fechaPago")
    @NotNull(message = "Debes introducir una fecha de pago")
    private LocalDateTime fechaPago;

    @OneToOne(mappedBy = "pago", cascade = CascadeType.ALL)
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "idMetodoPago", foreignKey = @ForeignKey(name = "fkPagosMetodos"), nullable = false)
    @NotNull(message = "Debes introducir un método de pago")
    private MetodosPago metodoPago;
}
