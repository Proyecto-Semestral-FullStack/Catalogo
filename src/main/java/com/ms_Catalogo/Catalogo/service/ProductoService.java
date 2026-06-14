package com.ms_Catalogo.Catalogo.service;

import com.ms_Catalogo.Catalogo.config.InventarioClient;
import com.ms_Catalogo.Catalogo.dto.ArchivoResponseDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoRequestDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoResponseDTO;
import com.ms_Catalogo.Catalogo.exception.RecursoNoEncontradoException;
import com.ms_Catalogo.Catalogo.model.Categoria;
import com.ms_Catalogo.Catalogo.model.Producto;
import com.ms_Catalogo.Catalogo.repository.CategoriaRepository;
import com.ms_Catalogo.Catalogo.repository.ProductoRepository;
import com.ms_Catalogo.Catalogo.config.StorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final StorageClient storageClient;   // Nuevo cliente de Storage
    private final InventarioClient inventarioClient;  // Nuevo cliente inventario --relacion bidirecional

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductos(String nombre, Long categoriaId,
                                                     BigDecimal minPrecio, BigDecimal maxPrecio,
                                                     String ordenar) {
        List<Producto> productos = productoRepository.buscarConFiltros(
                nombre, categoriaId, minPrecio, maxPrecio, ordenar);
        return productos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID " + id + " no encontrado"));
        return convertirADto(producto);
    }


    public ProductoResponseDTO crearProducto(ProductoRequestDTO dto, MultipartFile archivo) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría con ID " + dto.getCategoriaId() + " no existe"));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(categoria);
        producto.setActivo(true);

        // Si se envió una imagen, la subimos a Storage y guardamos el ID
        if (archivo != null && !archivo.isEmpty()) {
            ArchivoResponseDTO imagen = storageClient.uploadFile(archivo);
            producto.setImagenId(imagen.getId());
        }

        producto = productoRepository.save(producto);
        log.info("Producto creado: id={}, nombre={}, imagenId={}", producto.getId(), producto.getNombre(), producto.getImagenId());
        return convertirADto(producto);
    }

    // Metodo actualizar modificado para recibir un archivo opcional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO dto, MultipartFile archivo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID " + id + " no encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría con ID " + dto.getCategoriaId() + " no existe"));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(categoria);

        // Si se envió una nueva imagen, se reemplaza
        if (archivo != null && !archivo.isEmpty()) {
            ArchivoResponseDTO imagen = storageClient.uploadFile(archivo);
            producto.setImagenId(imagen.getId());
        }

        producto = productoRepository.save(producto);
        log.info("Producto actualizado: id={}, imagenId={}", producto.getId(), producto.getImagenId());
        return convertirADto(producto);
    }

    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID " + id + " no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
        log.info("Producto desactivado: id={}", id);
    }

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


    public ProductoResponseDTO asignarImagen(Long productoId, MultipartFile archivo) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        ArchivoResponseDTO imagen = storageClient.uploadFile(archivo);
        producto.setImagenId(imagen.getId());
        log.info("Imagen asignada al producto {}: imagenId={}", productoId, imagen.getId());
        productoRepository.save(producto);
        return convertirADto(producto);
    }
}
