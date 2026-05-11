package com.ms_Catalogo.Catalogo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    //Generar el id autoincremental o GeneratedbyId

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long Id ;

    @Column(nullable = false,unique = true,length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;


    //Aqui va el Join con la tabla Producto

    //Una categoria puede pertenecer a muchos productos
    @OneToMany(mappedBy = "categoria",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

}
