package com.ms_Catalogo.Catalogo.repository;

import com.ms_Catalogo.Catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto,Long> {

    @Query("SELECT p FROM Producto p " +
            "LEFT JOIN FETCH p.categoria " +
            "WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
            "(:minPrecio IS NULL OR p.precio >= :minPrecio) AND " +
            "(:maxPrecio IS NULL OR p.precio <= :maxPrecio) " +
            "ORDER BY " +
            "CASE WHEN :ordenar = 'precio' THEN p.precio END ASC, " +
            "CASE WHEN :ordenar = 'precio_desc' THEN p.precio END DESC, " +
            "p.nombre ASC")
    List<Producto> buscarConFiltros(@Param("nombre") String nombre,
                                    @Param("categoriaId") Long categoriaId,
                                    @Param("minPrecio") BigDecimal minPrecio,
                                    @Param("maxPrecio") BigDecimal maxPrecio,
                                    @Param("ordenar") String ordenar);


    // Búsqueda por nombre ignorando mayúsculas/minúsculas
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Filtro por categoría
    List<Producto> findByCategoriaId(Long categoriaId);

    // Ordenar por precio ascendente
    List<Producto> findAllByOrderByPrecioAsc();

    // Combinar nombre y categoría
    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaId(String nombre, Long categoriaId);
}
