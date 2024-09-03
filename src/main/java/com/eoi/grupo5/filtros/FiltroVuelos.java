package com.eoi.grupo5.filtros;

import java.time.LocalDateTime;

public record FiltroVuelos(String origenNombre, String destinoNombre, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
}
