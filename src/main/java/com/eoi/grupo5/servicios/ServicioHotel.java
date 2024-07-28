package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Hotel;
import com.eoi.grupo5.modelos.Vuelo;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.paginacion.PaginaRespuestaHoteles;
import com.eoi.grupo5.paginacion.PaginaRespuestaVuelos;
import com.eoi.grupo5.repos.RepoHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ServicioHotel extends AbstractBusinessServiceSoloEnt<Hotel, Integer, RepoHotel>{

    protected ServicioHotel(RepoHotel repoHotel) {
        super(repoHotel);
    }

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

    public List<Hotel> obtenerHotelesEnTuZona(Hotel hotel) {
        return super.buscarEntidades()
                .stream()
                .filter(h -> h.getLocalizacion().getNombre().equals(hotel.getLocalizacion().getNombre()) && !Objects.equals(h.getId(), hotel.getId()))
                .limit(2)
                .toList();
    }




}
