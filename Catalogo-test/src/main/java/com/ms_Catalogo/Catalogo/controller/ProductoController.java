package com.ms_Catalogo.Catalogo.controller;

import com.ms_Catalogo.Catalogo.dto.ProductoRequestDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoResponseDTO;
import com.ms_Catalogo.Catalogo.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    // Listar productos (sin cambios)
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal minPrecio,
            @RequestParam(required = false) BigDecimal maxPrecio,
            @RequestParam(required = false, defaultValue = "nombre") String ordenar) {

        List<ProductoResponseDTO> productos = productoService.listarProductos(
                nombre, categoriaId, minPrecio, maxPrecio, ordenar);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    // Obtener producto por ID (sin cambios)
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtener(@PathVariable Long id) {
        ProductoResponseDTO dto = productoService.obtenerProducto(id);
        return ResponseEntity.ok(dto);
    }

    // Crear producto – solo JSON (sin imagen)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        // No se recibe archivo, se crea el producto con imagenId = null
        ProductoResponseDTO creado = productoService.crearProducto(dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Subir imagen a un producto existente – multipart con solo archivo
    @PostMapping(path = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDTO> subirImagen(@PathVariable Long id,
                                                           @RequestPart("archivo") MultipartFile archivo) {
        ProductoResponseDTO actualizado = productoService.asignarImagen(id,archivo);
        return ResponseEntity.ok(actualizado);
    }

    // Actualizar producto – solo JSON (sin imagen)
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO actualizado = productoService.actualizarProducto(id, dto, null);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar producto (soft delete, sin cambios)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

}
