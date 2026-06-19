package com.ms_Catalogo.Catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos de una categoría retornados por el sistema")
public class CategoriaResponseDTO {

    @Schema(description = "ID único de la categoría", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "Figuras de acción")
    private String nombre;

    @Schema(description = "Descripción de la categoría", example = "Figuras coleccionables de anime y videojuegos")
    private String descripcion;
}
