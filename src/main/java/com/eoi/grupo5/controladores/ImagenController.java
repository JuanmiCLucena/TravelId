package com.eoi.grupo5.controladores;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

/**
 * El controlador `ImagenController` gestiona las solicitudes para acceder a imágenes almacenadas en el servidor.
 * Permite la lectura de imágenes a partir de su nombre de archivo y su entrega al cliente con el tipo de contenido adecuado.
 *
 * Funcionalidades principales:
 * - **Leer Imagen**: Permite la lectura y el envío de imágenes almacenadas en el servidor. El tipo de contenido (MIME type) de la imagen
 *   se determina en función de la extensión del archivo (JPG o PNG).
 *
 * Dependencias:
 * - `directorio.ficheros`: Ruta al directorio donde se almacenan las imágenes. Esta propiedad se configura en el archivo de configuración
 *   de la aplicación (por ejemplo, `application.properties` o `application.yml`).
 *
 * @see FileUtils
 * @see FilenameUtils
 */
@Controller
@RequestMapping("/imagenes")
public class ImagenController {

    @Value("${directorio.ficheros}")
    private String filePathRoot;

    /**
     * Lee una imagen desde el directorio de archivos y la envía al cliente con el tipo de contenido adecuado.
     *
     * @param imagen Nombre del archivo de imagen a leer, incluyendo la extensión.
     * @return Una respuesta HTTP con el contenido de la imagen y el tipo de contenido adecuado (JPEG o PNG).
     */
    @GetMapping(
            value = "/{imagen}"
        )
    public ResponseEntity<byte[]> leerImagen(@PathVariable("imagen") String imagen) {
        byte[] image = new byte[0];
        try {
            // Leer el archivo de imagen desde el sistema de archivos
            image = FileUtils.readFileToByteArray(new File(filePathRoot + "/" + imagen));
        }catch (IOException e) {
            e.printStackTrace();
        }

        if(FilenameUtils.getExtension(imagen).equals("jpg")){
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        }else {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
        }
    }
}
