package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
import com.eoi.grupo5.repos.RepoHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Servicio para gestionar las operaciones relacionadas con los hoteles.
 * Proporciona métodos para manejar el CRUD y realizar búsquedas paginadas,
 * así como obtener hoteles en la misma zona geográfica.
 */
@Service
public class ServicioHotel extends AbstractBusinessServiceSoloEnt<Hotel, Integer, RepoHotel> {

    /**
     * Constructor del servicio de hoteles.
     *
     * @param repoHotel el repositorio de hoteles.
     */
    protected ServicioHotel(RepoHotel repoHotel) {
        super(repoHotel);
    }

    /**
     * Busca las entidades de hoteles de forma paginada.
     *
     * @param page el número de página a buscar.
     * @param size el tamaño de la página (cantidad de elementos).
     * @return la respuesta paginada de hoteles.
     */
    public PaginaRespuestaHoteles<Hotel> buscarEntidadesPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotelPage = getRepo().findAll((Specification<Hotel>) null, pageable);

        PaginaRespuestaHoteles<Hotel> respuesta = new PaginaRespuestaHoteles<>();
        respuesta.setContent(hotelPage.getContent());
        respuesta.setSize(hotelPage.getSize());
        respuesta.setTotalSize(hotelPage.getTotalElements());
        respuesta.setPage(hotelPage.getNumber());
        respuesta.setTotalPages(hotelPage.getTotalPages());

        return respuesta;
    }

    /**
     * Obtiene una lista de hoteles en la misma zona geográfica que el hotel especificado.
     * Limita el resultado a un máximo de 2 hoteles distintos del hotel proporcionado.
     *
     * @param hotel el hotel cuya zona se desea consultar.
     * @return una lista de hoteles en la misma zona, excluyendo el hotel actual.
     */
    public List<Hotel> obtenerHotelesEnTuZona(Hotel hotel) {
        return super.buscarEntidades()
                .stream()
                .filter(h -> h.getLocalizacion().getNombre().equals(hotel.getLocalizacion().getNombre()) && !Objects.equals(h.getId(), hotel.getId()))
                .limit(2)
                .toList();
    }
}
