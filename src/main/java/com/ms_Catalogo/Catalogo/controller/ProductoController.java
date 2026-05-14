package com.ms_Catalogo.Catalogo.controller;

import com.ms_Catalogo.Catalogo.dto.ProductoRequestDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoResponseDTO;
import com.ms_Catalogo.Catalogo.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal minPrecio,
            @RequestParam(required = false) BigDecimal maxPrecio,
            @RequestParam(required = false, defaultValue = "nombre") String ordenar)
    {
        List<ProductoResponseDTO> productos = productoService.listarProductos(
                nombre, categoriaId, minPrecio, maxPrecio, ordenar);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtener(@PathVariable Long id) {
        ProductoResponseDTO dto = productoService.obtenerProducto(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO creado = productoService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO actualizado = productoService.actualizarProducto(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

}
