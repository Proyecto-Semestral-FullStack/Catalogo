package com.ms_Catalogo.Catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos de un producto retornados por el sistema")
public class ProductoResponseDTO {

    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Figura Goku Super Saiyan Blue")
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Figura articulada de 30cm con base iluminada")
    private String descripcion;

    @Schema(description = "Precio del producto en CLP", example = "15990")
    private BigDecimal precio;

    @Schema(description = "Nombre de la categoría a la que pertenece el producto", example = "Figuras de acción")
    private String nombreCategoria; // Para mostrar el nombre en lugar del ID

    @Schema(description = "ID de la imagen en ms-storage. El frontend construye la URL a partir de este ID", example = "3")
    private Long imagenId;   // ID en storage, el front construye la URL

    @Schema(description = "Indica si el producto está activo (false = eliminado con soft delete)", example = "true")
    private Boolean activo;
}
