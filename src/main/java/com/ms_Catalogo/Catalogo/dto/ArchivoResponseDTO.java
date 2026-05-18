package com.ms_Catalogo.Catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoResponseDTO {
    private Long id;
    private String nombreOriginal;
    private String urlPublica;
}
