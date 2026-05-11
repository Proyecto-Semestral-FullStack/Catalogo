package com.ms_Catalogo.Catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String nombreCategoria; // Para mostrar el nombre en lugar del ID
    private Long imagenId;   // ID en storage, el front construye la URL
    private Boolean activo;
}
