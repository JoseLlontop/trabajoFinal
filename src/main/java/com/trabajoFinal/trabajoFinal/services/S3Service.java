package com.trabajoFinal.trabajoFinal.services;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.trabajoFinal.trabajoFinal.models.virtualModel.Asset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final static String BUCKET ="contenedor-aplicacion-tienda";

    @Autowired
    private AmazonS3Client s3Client;

    //Enviar un objeto al bucket
    public String putObject(MultipartFile multipartFile) {

        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        String key = String.format("%s.%s", UUID.randomUUID(), extension);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize()); // Establecer la longitud del contenido

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, key, multipartFile.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putObjectRequest);
            return key;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    //Obtener el objeto desde el bucket
    public Asset getObject(String key) {
        S3Object s3Object = s3Client.getObject(BUCKET, key);
        ObjectMetadata metadata = s3Object.getObjectMetadata();

        try {
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);

            return new Asset(bytes, metadata.getContentType());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    //Eliminar un objeto del bucket
    public void deleteObject(String key) {
        s3Client.deleteObject(BUCKET, key);
    }

    //Construir una URL para los objetos publicos
    public String getObjectURL(String key){
        return String.format("https://%s.s3.amazonaws.com/%s", BUCKET, key);
    }
}