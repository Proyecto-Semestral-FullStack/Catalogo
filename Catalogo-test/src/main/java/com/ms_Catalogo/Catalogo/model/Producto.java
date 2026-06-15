package com.ms_Catalogo.Catalogo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Table(name = "producto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @Column(name = "nombre_producto",nullable = false,length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT" )
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    //JOIN ENTRE TABLAS PRODUCTO Y CATEGORIA

    /**AVERIGUAR COMO FUNCIONA ESTO ...**/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id",nullable = false)
    private Categoria categoria;


    // RelaciOn con storage: guardamos el ID del archivo en MediaStorage
    @Column(name = "imagen_id")
    private Long imagenId;  // [REF LOGICA] a ms-MediaStorage.ARCHIVO_MEDIA.id--luego se ve eso cuando se conecte a Storage

    // Atributo de control --Para desactivar un producto si esta disponible o no en caso de que no haya stock o etc.
    @Column(nullable = false)
    private Boolean activo = true;



}
