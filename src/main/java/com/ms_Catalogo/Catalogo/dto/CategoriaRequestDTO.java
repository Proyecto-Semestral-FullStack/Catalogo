package com.ms_Catalogo.Catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para crear o actualizar una categoría")
public class CategoriaRequestDTO {

    @Schema(description = "Nombre de la categoría", example = "Figuras de acción", maxLength = 100)
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;


    @Schema(description = "Descripción opcional de la categoría", example = "Figuras coleccionables de anime y videojuegos", maxLength = 500)
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
}
