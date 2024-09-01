package com.eoi.grupo5.servicios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

/**
 * Clase abstracta que proporciona servicios de negocio básicos para operaciones CRUD sobre una entidad específica.
 *
 * Esta clase abstracta define operaciones comunes que se pueden realizar en cualquier entidad que tenga un repositorio
 * que extienda {@link JpaRepository}. Las operaciones incluyen búsqueda, guardado y eliminación de entidades.
 *
 * @param <E>  el tipo de la entidad que maneja el servicio.
 * @param <ID> el tipo de identificador de la entidad.
 * @param <REPO> el tipo del repositorio que extiende {@link JpaRepository}.
 */
public abstract class AbstractBusinessServiceSoloEnt<E, ID, REPO extends JpaRepository<E, ID>> {

    private final REPO repo;

    /**
     * Constructor que inicializa el servicio con el repositorio proporcionado.
     *
     * @param repo el repositorio que se utilizará para las operaciones CRUD.
     */
    protected AbstractBusinessServiceSoloEnt(REPO repo) {
        this.repo = repo;
    }

    /**
     * Recupera todas las entidades en forma de lista.
     *
     * @return una lista que contiene todas las entidades en la base de datos.
     */
    public List<E> buscarEntidades() {
        return this.repo.findAll();
    }

    /**
     * Recupera todas las entidades en forma de conjunto (Set), eliminando duplicados si existen.
     *
     * @return un conjunto que contiene todas las entidades en la base de datos.
     */
    public Set<E> buscarEntidadesSet() {
        return new HashSet<>(this.repo.findAll());
    }

    /**
     * Busca una entidad por su identificador.
     *
     * @param id el identificador de la entidad.
     * @return un {@link Optional} que contiene la entidad si se encuentra, o vacío si no se encuentra.
     */
    public Optional<E> encuentraPorIdEntity(ID id) {
        return this.repo.findById(id);
    }

    /**
     * Busca una entidad por su identificador.
     *
     * @param id el identificador de la entidad.
     * @return un {@link Optional} que contiene la entidad si se encuentra, o vacío si no se encuentra.
     */
    public Optional<E> encuentraPorId(ID id) {
        return this.repo.findById(id);
    }

    /**
     * Busca todas las entidades con paginación.
     *
     * @param pageable el objeto {@link Pageable} que define la página y el tamaño de la página.
     * @return un {@link Page} que contiene la página actual de entidades.
     */
    public Page<E> buscarTodos(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /**
     * Recupera todas las entidades en forma de conjunto (Set), eliminando duplicados si existen.
     *
     * @return un conjunto que contiene todas las entidades en la base de datos.
     */
    public Set<E> buscarTodosSet() {
        return new HashSet<>(this.repo.findAll());
    }

    /**
     * Guarda una entidad en la base de datos.
     *
     * @param entidad la entidad a guardar.
     * @return la entidad guardada.
     * @throws Exception si ocurre algún problema durante el guardado.
     */
    public E guardar(E entidad) throws Exception {
        // Guarda la entidad en la base de datos
        return repo.save(entidad);
    }

    /**
     * Guarda una lista de entidades en la base de datos.
     *
     * @param ents la lista de entidades a guardar.
     * @throws Exception si ocurre algún problema durante el guardado.
     */
    public void guardar(List<E> ents) throws Exception {
        for (E e : ents) {
            // Guarda cada entidad en la base de datos
            repo.save(e);
        }
    }

    /**
     * Elimina una entidad por su identificador.
     *
     * @param id el identificador de la entidad a eliminar.
     */
    public void eliminarPorId(ID id) {
        this.repo.deleteById(id);
    }

    /**
     * Obtiene el repositorio asociado con este servicio.
     *
     * @return el repositorio asociado con este servicio.
     */
    public REPO getRepo() {
        return repo;
    }
}
