package com.ms_Catalogo.Catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos del archivo retornados por ms-storage tras una subida exitosa")
public class ArchivoResponseDTO {
    @Schema(description = "ID único del archivo en ms-storage", example = "3")
    private Long id;

    @Schema(description = "Nombre original del archivo subido", example = "goku_ssblue.png")
    private String nombreOriginal;

    @Schema(description = "URL pública del archivo en Google Cloud Storage", example = "https://storage.googleapis.com/frikitienda_img/goku_ssblue.png")
    private String urlPublica;
}
