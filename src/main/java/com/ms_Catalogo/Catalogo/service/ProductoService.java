package com.ms_Catalogo.Catalogo.service;

import com.ms_Catalogo.Catalogo.dto.ProductoRequestDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoResponseDTO;
import com.ms_Catalogo.Catalogo.exception.RecursoNoEncontradoException;
import com.ms_Catalogo.Catalogo.model.Categoria;
import com.ms_Catalogo.Catalogo.model.Producto;
import com.ms_Catalogo.Catalogo.repository.CategoriaRepository;
import com.ms_Catalogo.Catalogo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    // Obtener todos (con opción de filtros simples)
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos(String nombre, Long categoriaId, String ordenar) {
        List<Producto> productos;

        if (nombre != null && !nombre.isEmpty() && categoriaId != null) {
            productos = productoRepository.findByNombreContainingIgnoreCaseAndCategoriaId(nombre, categoriaId);
        } else if (nombre != null && !nombre.isEmpty()) {
            productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (categoriaId != null) {
            productos = productoRepository.findByCategoriaId(categoriaId);
        } else if ("precio".equalsIgnoreCase(ordenar)) {
            productos = productoRepository.findAllByOrderByPrecioAsc();
        } else {
            productos = productoRepository.findAll();
        }

        return productos.stream().map(this::convertirADto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID " + id + " no encontrado"));
        return convertirADto(producto);
    }

    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto) {
        // Validar que la categoría exista (FK real, pero podemos hacer validación explícita)
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría con ID " + dto.getCategoriaId() + " no existe"));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(categoria);
        producto.setActivo(true);
        // Imagen queda null por defecto, luego se puede actualizar con WebClient

        producto = productoRepository.save(producto);
        log.info("Producto creado: id={}, nombre={}", producto.getId(), producto.getNombre());
        return convertirADto(producto);
    }

    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID " + id + " no encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría con ID " + dto.getCategoriaId() + " no existe"));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(categoria);

        producto = productoRepository.save(producto);
        log.info("Producto actualizado: id={}", producto.getId());
        return convertirADto(producto);
    }

    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID " + id + " no encontrado"));
        // En lugar de borrado físico, se puede desactivar (soft delete)
        producto.setActivo(false);
        productoRepository.save(producto);
        log.info("Producto desactivado: id={}", id);
    }

    // Método para asignar una imagen desde Storage
    public void asignarImagen(Long productoId, Long imagenId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        producto.setImagenId(imagenId);
        productoRepository.save(producto);
        log.info("Imagen asignada al producto {}: imagenId={}", productoId, imagenId);
    }

    // Conversión interna a DTO
    private ProductoResponseDTO convertirADto(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .nombreCategoria(producto.getCategoria().getNombre())
                .imagenId(producto.getImagenId())
                .activo(producto.getActivo())
                .build();
    }
}
