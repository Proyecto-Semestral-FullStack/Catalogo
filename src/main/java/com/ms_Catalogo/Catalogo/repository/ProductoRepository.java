package com.ms_Catalogo.Catalogo.repository;

import com.ms_Catalogo.Catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto,Long> {

    // Búsqueda por nombre ignorando mayúsculas/minúsculas
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Filtro por categoría
    List<Producto> findByCategoriaId(Long categoriaId);

    // Ordenar por precio ascendente
    List<Producto> findAllByOrderByPrecioAsc();

    // Combinar nombre y categoría
    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaId(String nombre, Long categoriaId);
}
