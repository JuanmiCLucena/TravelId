package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.paginacion.PaginaRespuestaLocalizaciones;
import com.eoi.grupo5.repos.RepoLocalizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para gestionar las operaciones relacionadas con las localizaciones.
 * Proporciona funcionalidades de CRUD a través de la herencia de {@link AbstractBusinessServiceSoloEnt},
 * así como operaciones adicionales específicas para las localizaciones.
 */
@Service
public class ServicioLocalizacion extends AbstractBusinessServiceSoloEnt<Localizacion, Integer, RepoLocalizacion> {

    /**
     * Constructor del servicio de localizaciones.
     *
     * @param repoLocalizacion el repositorio de localizaciones.
     */
    protected ServicioLocalizacion(RepoLocalizacion repoLocalizacion) {
        super(repoLocalizacion);
    }

    /**
     * Busca las entidades de localizaciones paginadas.
     *
     * @param page el número de página.
     * @param size el tamaño de la página.
     * @return una instancia de {@link PaginaRespuestaLocalizaciones} que contiene la lista de localizaciones paginadas.
     */
    public PaginaRespuestaLocalizaciones<Localizacion> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Localizacion> localizacionPage = getRepo().findAll(pageable);

        PaginaRespuestaLocalizaciones<Localizacion> respuesta = new PaginaRespuestaLocalizaciones<>();
        respuesta.setContent(localizacionPage.getContent());
        respuesta.setSize(localizacionPage.getSize());
        respuesta.setTotalSize(localizacionPage.getTotalElements());
        respuesta.setPage(localizacionPage.getNumber());
        respuesta.setTotalPages(localizacionPage.getTotalPages());

        return respuesta;
    }

    /**
     * Busca localizaciones cuyo nombre contenga la cadena proporcionada, ignorando mayúsculas y minúsculas.
     *
     * @param query la cadena de búsqueda.
     * @return una lista de nombres de localizaciones que contienen la cadena de búsqueda.
     */
    public List<String> findByNombreContaining(String query) {
        List<Localizacion> localizaciones = getRepo().findByNombreContainingIgnoreCase(query);

        // Devuelve solo los nombres de las localizaciones
        return localizaciones.stream()
                .map(Localizacion::getNombre)
                .toList();
    }

}
