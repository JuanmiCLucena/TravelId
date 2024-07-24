package com.eoi.grupo5.repos.filtros;

import java.time.LocalDateTime;


public record FiltroActividades(Integer tipoId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
}
