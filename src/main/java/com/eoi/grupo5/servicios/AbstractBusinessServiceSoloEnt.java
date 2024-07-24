package com.eoi.grupo5.servicios;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;


/**
 * The type Abstract business service solo ent.
 *
 * @param <E>    the type parameter
 * @param <ID>   the type parameter
 * @param <REPO> the type parameter
 */
public abstract class AbstractBusinessServiceSoloEnt<E, ID, REPO extends JpaRepository<E, ID>> {

    private final REPO repo;

    /**
     * Instantiates a new Abstract business service solo ent.
     *
     * @param repo the repo
     */
    protected AbstractBusinessServiceSoloEnt(REPO repo) {
        this.repo = repo;
    }

    /**
     * Buscar entidades list.
     *
     * @return the list
     */
    public List<E> buscarEntidades() {
        return this.repo.findAll();
    }

    /**
     * Buscar entidades set set.
     *
     * @return the set
     */
    public Set<E> buscarEntidadesSet() {
        Set<E> eSet = new HashSet<E>(this.repo.findAll());
        return eSet;
    }

    /**
     * Encuentra por id entity optional.
     *
     * @param id the id
     * @return the optional
     */
    public Optional<E> encuentraPorIdEntity(ID id) {
        return this.repo.findById(id);
    }

    /**
     * Encuentra por id optional.
     *
     * @param id the id
     * @return the optional
     */
    public Optional<E> encuentraPorId(ID id) {
        return this.repo.findById(id);
    }

    /**
     * Buscar todos page.
     *
     * @param pageable the pageable
     * @return the page
     */
    public Page<E> buscarTodos(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /**
     * Buscar todos set set.
     *
     * @return the set
     */
    public Set<E> buscarTodosSet() {
        Set<E> eSet = new HashSet<E>(this.repo.findAll());
        return eSet;
    }

    /**
     * Guardar e.
     *
     * @param entidad the entidad
     * @return the e
     * @throws Exception the exception
     */
//Guardar
    public E guardar(E entidad) throws Exception {
        //Guardo el la base de datos
        E entidadGuardada = repo.save(entidad);
        //Traducir la entidad a DTO para devolver el DTO
        return entidadGuardada;
    }

    /**
     * Guardar.
     *
     * @param ents the ents
     * @throws Exception the exception
     */
    public void guardar(List<E> ents) throws Exception {
        Iterator<E> it = ents.iterator();
        // mientras al iterador queda proximo juego
        while (it.hasNext()) {
            //Obtenemos la password de a entidad
            //Datos del usuario
            E e = it.next();
            repo.save(e);
        }
    }

    /**
     * Eliminar por id.
     *
     * @param id the id
     */
    //eliminar un registro
    public void eliminarPorId(ID id) {
        this.repo.deleteById(id);
    }

    /**
     * Gets repo.
     *
     * @return the repo
     */
    //Obtener el repo
    public REPO getRepo() {
        return repo;
    }
}
