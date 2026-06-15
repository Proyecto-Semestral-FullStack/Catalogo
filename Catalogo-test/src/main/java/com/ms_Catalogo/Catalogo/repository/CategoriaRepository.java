package com.ms_Catalogo.Catalogo.repository;

import com.ms_Catalogo.Catalogo.model.Categoria;
import com.ms_Catalogo.Catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {

    Optional<Categoria> findByNombre(String nombre);

}
