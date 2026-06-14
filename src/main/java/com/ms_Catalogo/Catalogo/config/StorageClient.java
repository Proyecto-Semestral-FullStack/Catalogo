package com.ms_Catalogo.Catalogo.config;

//Es para que se conecte con storage subir imagenes es el unico servicio que consume directaente Catalogo

//Es por eso que no tiene mas Webclients pero si los otros servicios tienen mas porque se comunican y consumen otros

import com.ms_Catalogo.Catalogo.dto.ArchivoResponseDTO;
import com.ms_Catalogo.Catalogo.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class StorageClient {

    private final WebClient webClient;

    public StorageClient(@LoadBalanced WebClient.Builder webClientBuilder,
                         @Value("${storage.service.url}") String storageUrl) {
        this.webClient = webClientBuilder.baseUrl(storageUrl).build();
    }

    public ArchivoResponseDTO uploadFile(MultipartFile archivo) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("archivo", archivo.getResource())
                .header("Content-Disposition",
                        "form-data; name=archivo; filename=" + archivo.getOriginalFilename());

        try {
            return webClient.post()
                    .uri("/api/archivos")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(ArchivoResponseDTO.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al subir archivo a Storage: {}", e.getMessage());
            throw new StorageException("No se pudo subir la imagen. El servicio de almacenamiento no está disponible en este momento.");
        }
    }
}
