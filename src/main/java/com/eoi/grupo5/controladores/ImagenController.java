package com.eoi.grupo5.controladores;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/imagenes")
public class ImagenController {

    @Value("${directorio.ficheros}")
    private String filePathRoot;

    @GetMapping(
            value = "/{imagen}"
        )
    public ResponseEntity<byte[]> leerImagen(@PathVariable("imagen") String imagen) {
        byte[] image = new byte[0];
        try {
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
