package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.*;
import com.eoi.grupo5.paginacion.PaginaRespuestaAsientos;
import com.eoi.grupo5.repos.RepoAsiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para gestionar las operaciones relacionadas con los asientos.
 * Extiende la clase {@link AbstractBusinessServiceSoloEnt} para proporcionar funcionalidad de negocio adicional.
 */
@Service
public class ServicioAsiento extends AbstractBusinessServiceSoloEnt<Asiento, Integer, RepoAsiento> {

    /**
     * Constructor del servicio que inyecta el repositorio de asientos.
     *
     * @param repoAsiento Repositorio de asientos que gestiona las operaciones de persistencia.
     */
    protected ServicioAsiento(RepoAsiento repoAsiento) {
        super(repoAsiento);
    }

    /**
     * Busca todas las entidades de asiento de forma paginada.
     *
     * @param page Número de página a obtener.
     * @param size Tamaño de la página.
     * @return Una instancia de {@link PaginaRespuestaAsientos} que contiene los asientos en la página solicitada.
     */
    public PaginaRespuestaAsientos<Asiento> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Asiento> asientoPage = getRepo().findAll(pageable);

        PaginaRespuestaAsientos<Asiento> respuesta = new PaginaRespuestaAsientos<>();
        respuesta.setContent(asientoPage.getContent());
        respuesta.setSize(asientoPage.getSize());
        respuesta.setTotalSize(asientoPage.getTotalElements());
        respuesta.setPage(asientoPage.getNumber());
        respuesta.setTotalPages(asientoPage.getTotalPages());

        return respuesta;
    }

    /**
     * Obtiene el precio actual de un asiento en función de la fecha actual.
     *
     * @param asiento     Asiento del cual se desea obtener el precio actual.
     * @param fechaActual Fecha actual utilizada para filtrar los precios.
     * @return El precio actual del asiento, o null si no se encuentra un precio válido.
     */
    public Precio getPrecioActual(Asiento asiento, LocalDateTime fechaActual) {
        return asiento.getPrecio().stream()
                .filter(precio -> !fechaActual.isBefore(precio.getFechaInicio()) && (precio.getFechaFin() == null || !fechaActual.isAfter(precio.getFechaFin())))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene un mapa con los precios actuales de los asientos en un vuelo determinado.
     *
     * @param vuelo Vuelo del cual se desean obtener los precios actuales de los asientos.
     * @return Un mapa que asocia el ID del asiento con su precio actual. Si no se encuentra un precio válido, el valor será null.
     */
    public Map<Integer, Double> obtenerPreciosActualesAsientosVuelo(Vuelo vuelo) {
        LocalDateTime fechaActual = LocalDateTime.now();
        Map<Integer, Double> preciosActuales = new HashMap<>();

        vuelo.getAsientos().forEach(asiento -> {
            Precio precioActual = getPrecioActual(asiento, fechaActual);
            if (precioActual != null) {
                preciosActuales.put(asiento.getId(), precioActual.getValor());
            } else {
                preciosActuales.put(asiento.getId(), null);
            }
        });

        return preciosActuales;
    }
}
