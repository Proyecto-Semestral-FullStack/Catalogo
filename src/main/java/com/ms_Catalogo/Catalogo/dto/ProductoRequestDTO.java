package com.ms_Catalogo.Catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Datos requeridos para crear o actualizar un producto")
public class ProductoRequestDTO {

    @Schema(description = "Nombre del producto", example = "Figura Goku Super Saiyan Blue", maxLength = 200)
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200)
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Figura articulada de 30cm con base iluminada", maxLength = 2000)
    @Size(max = 2000)
    private String descripcion;

    @Schema(description = "Precio del producto en CLP (debe ser mayor a 0)", example = "15990")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Schema(description = "ID de la categoría a la que pertenece el producto", example = "2")
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    // No incluimos imagenId porque la imagen se maneja aparte vía storage

}
