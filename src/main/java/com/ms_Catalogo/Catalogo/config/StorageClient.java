package com.ms_Catalogo.Catalogo.config;

import com.ms_Catalogo.Catalogo.dto.ArchivoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;


@FeignClient(name = "ms-storage", configuration = FeignMultipartConfig.class)
public interface StorageClient {
    @PostMapping(value = "/api/archivos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ArchivoResponseDTO uploadFile(@RequestPart("archivo") MultipartFile archivo);
}
