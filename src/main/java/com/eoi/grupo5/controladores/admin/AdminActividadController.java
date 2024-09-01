package com.eoi.grupo5.controladores.admin;

import com.eoi.grupo5.dtos.ActividadFormDto;
import com.eoi.grupo5.modelos.Actividad;
import com.eoi.grupo5.modelos.Imagen;
import com.eoi.grupo5.modelos.Localizacion;
import com.eoi.grupo5.modelos.TipoActividad;
import com.eoi.grupo5.paginacion.PaginaRespuestaActividades;
import com.eoi.grupo5.servicios.*;
import com.eoi.grupo5.servicios.archivos.FileSystemStorageService;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la administración de actividades en el sistema.
 * Proporciona las operaciones CRUD para gestionar actividades, así como la gestión de imágenes asociadas.
 *
 * <p>Este controlador maneja rutas bajo el contexto "/admin/actividades".</p>
 */
@Controller
@RequestMapping("/admin/actividades")
public class AdminActividadController {

    private final ServicioActividad servicioActividad;
    private final ServicioTipoActividad servicioTipoActividad;
    private final ServicioImagen servicioImagen;
    private final ServicioLocalizacion servicioLocalizacion;
    private final FileSystemStorageService fileSystemStorageService;

    /**
     * Constructor para inicializar los servicios requeridos por el controlador.
     *
     * @param servicioActividad        Servicio para la gestión de actividades.
     * @param servicioTipoActividad    Servicio para la gestión de tipos de actividades.
     * @param servicioImagen           Servicio para la gestión de imágenes.
     * @param servicioLocalizacion     Servicio para la gestión de localizaciones.
     * @param fileSystemStorageService Servicio para la gestión del almacenamiento de archivos.
     */
    public AdminActividadController(ServicioActividad servicioActividad, ServicioTipoActividad servicioTipoActividad, ServicioImagen servicioImagen, ServicioLocalizacion servicioLocalizacion, FileSystemStorageService fileSystemStorageService) {
        this.servicioActividad = servicioActividad;
        this.servicioTipoActividad = servicioTipoActividad;
        this.servicioImagen = servicioImagen;
        this.servicioLocalizacion = servicioLocalizacion;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    /**
     * Muestra la lista de actividades en una página de administración con paginación.
     *
     * @param modelo Objeto Model para agregar atributos a la vista.
     * @param page   Número de página para la paginación (por defecto 0).
     * @param size   Tamaño de la página para la paginación (por defecto 10).
     * @return El nombre de la vista que muestra la lista de actividades.
     */
    @GetMapping
    public String listar(
            Model modelo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginaRespuestaActividades<Actividad> actividadesPage = servicioActividad.buscarEntidadesPaginadas(page, size);
        List<Actividad> actividades = actividadesPage.getContent();
        modelo.addAttribute("actividades", actividades);
        modelo.addAttribute("page", actividadesPage);
        return "admin/actividades/adminActividades";
    }

    /**
     * Muestra el formulario para crear una nueva actividad.
     *
     * @param modelo Objeto Model para agregar atributos a la vista.
     * @return El nombre de la vista que contiene el formulario de creación de actividad.
     */
    @GetMapping("/crear")
    public String mostrarPaginaCrear(Model modelo) {
        ActividadFormDto actividad = new ActividadFormDto();
        modelo.addAttribute("actividadFormDto", actividad);
        modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
        modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());
        return "admin/actividades/adminNuevaActividad";
    }

    /**
     * Procesa la creación de una nueva actividad.
     *
     * @param actividadFormDto DTO que contiene los datos del formulario para la creación de la actividad.
     * @param result           Objeto BindingResult para manejar errores de validación.
     * @param modelo           Objeto Model para agregar atributos a la vista.
     * @return Redirección a la lista de actividades si la creación es exitosa, o la vista de creación si hay errores.
     */
    @PostMapping("/crear")
    public String crear(
            @Valid @ModelAttribute("actividadFormDto") ActividadFormDto actividadFormDto,
            BindingResult result,
            Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());
            return "admin/actividades/adminNuevaActividad";
        }

        try {
            // Crear la actividad
            Actividad actividad = new Actividad();
            actividad.setNombre(actividadFormDto.getNombre());
            actividad.setDescripcion(actividadFormDto.getDescripcion());
            actividad.setFechaInicio(actividadFormDto.getFechaInicio());
            actividad.setFechaFin(actividadFormDto.getFechaFin());
            actividad.setMaximosAsistentes(actividadFormDto.getMaximosAsistentes());
            actividad.setAsistentesConfirmados(actividadFormDto.getAsistentesConfirmados());

            Optional<Localizacion> optLocalizacion = servicioLocalizacion.encuentraPorId(actividadFormDto.getLocalizacionId());
            Optional<TipoActividad> optTipo = servicioTipoActividad.encuentraPorId(actividadFormDto.getTipoId());

            optLocalizacion.ifPresent(actividad::setLocalizacion);
            optTipo.ifPresent(actividad::setTipo);

            // Guardar la actividad sin imágenes primero
            Actividad actividadGuardada = servicioActividad.guardar(actividad);

            // Procesar las imágenes
            if (actividadFormDto.getImagenes() != null) {
                for (MultipartFile archivo : actividadFormDto.getImagenes()) {
                    if (!archivo.isEmpty()) {
                        Imagen imagenBD = new Imagen();
                        imagenBD.setActividad(actividadGuardada);
                        String FILE_NAME = "actividad-" + actividadGuardada.getId() + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(archivo.getOriginalFilename());
                        imagenBD.setUrl(FILE_NAME);

                        // Almacenar la imagen en el sistema de archivos
                        fileSystemStorageService.store(archivo, FILE_NAME);

                        // Agregar la imagen a la actividad
                        actividadGuardada.getImagenes().add(imagenBD);

                        // Guardar la imagen en la base de datos
                        servicioImagen.guardar(imagenBD);
                    }
                }
            }

            // Guardar la actividad con las nuevas imágenes
            servicioActividad.guardar(actividadGuardada);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/actividades";
    }

    /**
     * Muestra el formulario para editar una actividad existente.
     *
     * @param id     ID de la actividad a editar.
     * @param modelo Objeto Model para agregar atributos a la vista.
     * @return El nombre de la vista que contiene el formulario de edición, o una página de error si la actividad no se encuentra.
     */
    @GetMapping("/editar/{id}")
    public String mostrarPaginaEditar(@PathVariable Integer id, Model modelo) {
        Optional<Actividad> actividadOptional = servicioActividad.encuentraPorId(id);
        if (actividadOptional.isPresent()) {
            Actividad actividad = actividadOptional.get();

            ActividadFormDto actividadFormDto = ActividadFormDto.from(actividad);

            // Añadir DTO y listas al modelo
            modelo.addAttribute("actividadFormDto", actividadFormDto);
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());

            return "admin/actividades/adminDetallesActividad";
        } else {
            // Actividad no encontrada - redirigir o mostrar una página de error
            return "error/paginaError";
        }
    }

    /**
     * Procesa la edición de una actividad existente.
     *
     * @param id                ID de la actividad a editar.
     * @param imagenes          Array de archivos MultipartFile que representan las imágenes nuevas a subir (opcional).
     * @param actividadFormDto  DTO que contiene los datos del formulario para la edición de la actividad.
     * @param result            Objeto BindingResult para manejar errores de validación.
     * @param modelo            Objeto Model para agregar atributos a la vista.
     * @return Redirección a la lista de actividades si la edición es exitosa, o la vista de edición si hay errores.
     * IOException si ocurre un error al almacenar las imágenes.
     */
    @PutMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @RequestParam(name = "imagenes", required = false) MultipartFile[] imagenes,
            @Valid @ModelAttribute("actividadFormDto") ActividadFormDto actividadFormDto,
            BindingResult result,
            Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("localizaciones", servicioLocalizacion.buscarEntidades());
            modelo.addAttribute("tipos", servicioTipoActividad.buscarEntidades());
            return "admin/actividades/adminDetallesActividad";
        }

        try {
            Optional<Actividad> actividadOptional = servicioActividad.encuentraPorId(id);
            if (actividadOptional.isPresent()) {
                Actividad actividadExistente = actividadOptional.get();

                // Actualizar los campos de la actividad existente con los nuevos valores
                actividadExistente.setNombre(actividadFormDto.getNombre());
                actividadExistente.setDescripcion(actividadFormDto.getDescripcion());
                actividadExistente.setFechaInicio(actividadFormDto.getFechaInicio());
                actividadExistente.setFechaFin(actividadFormDto.getFechaFin());
                actividadExistente.setLocalizacion(servicioLocalizacion.encuentraPorId(actividadFormDto.getLocalizacionId()).orElse(null));
                actividadExistente.setTipo(servicioTipoActividad.encuentraPorId(actividadFormDto.getTipoId()).orElse(null));
                actividadExistente.setMaximosAsistentes(actividadFormDto.getMaximosAsistentes());
                actividadExistente.setAsistentesConfirmados(actividadFormDto.getAsistentesConfirmados());

                // Maneja las imágenes si se han subido nuevas
                if (imagenes != null && imagenes.length > 0) {
                    boolean algunaImagenNueva = false;

                    // Verifica si hay alguna imagen no vacía
                    for (MultipartFile archivo : imagenes) {
                        if (!archivo.isEmpty()) {
                            algunaImagenNueva = true;
                            break;
                        }
                    }

                    if (algunaImagenNueva) {
                        // Limpiar imágenes existentes solo si hay nuevas imágenes
                        actividadExistente.getImagenes().clear();

                        for (MultipartFile archivo : imagenes) {
                            if (!archivo.isEmpty()) {
                                Imagen imagenBD = new Imagen();
                                imagenBD.setActividad(actividadExistente);

                                // Crear un nombre único para la imagen
                                String FILE_NAME = "actividad-" + actividadExistente.getId() + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(archivo.getOriginalFilename());
                                imagenBD.setUrl(FILE_NAME);

                                // Almacenar la imagen en el sistema de archivos
                                fileSystemStorageService.store(archivo, FILE_NAME);

                                // Agregar la imagen a la actividad
                                actividadExistente.getImagenes().add(imagenBD);

                                // Guardar la imagen en la base de datos
                                servicioImagen.guardar(imagenBD);
                            }
                        }
                    }
                }

                // Guardar la actividad actualizada
                servicioActividad.guardar(actividadExistente);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al actualizar la actividad", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return "redirect:/admin/actividades";
    }

    /**
     * Elimina una actividad existente.
     *
     * @param id ID de la actividad a eliminar.
     * @return Redirección a la lista de actividades tras la eliminación.
     */
    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        Optional<Actividad> optionalActividad = servicioActividad.encuentraPorId(id);
        if (optionalActividad.isPresent()) {
            servicioActividad.eliminarPorId(id);
        }
        return "redirect:/admin/actividades";
    }
}
