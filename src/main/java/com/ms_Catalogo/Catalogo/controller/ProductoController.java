package com.ms_Catalogo.Catalogo.controller;

import com.ms_Catalogo.Catalogo.dto.ProductoRequestDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoResponseDTO;
import com.ms_Catalogo.Catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Productos", description = "Gestión del catálogo de productos")  // agrupa endpoints
public class ProductoController {
    private final ProductoService productoService;

    @Operation(summary = "Listar productos", description = "Retorna la lista de productos con filtros opcionales por nombre, categoría y rango de precio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos retornada exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay productos que coincidan con los filtros")
    })
    // Listar productos
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar(
            @Parameter(description = "Filtrar por nombre (búsqueda parcial)", example = "Goku")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar por ID de categoría", example = "2")
            @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Precio mínimo del producto", example = "1000")
            @RequestParam(required = false) BigDecimal minPrecio,
            @Parameter(description = "Precio máximo del producto", example = "50000")
            @RequestParam(required = false) BigDecimal maxPrecio,
            @Parameter(description = "Campo por el que ordenar los resultados (nombre, precio)", example = "nombre")
            @RequestParam(required = false, defaultValue = "nombre") String ordenar) {

        List<ProductoResponseDTO> productos = productoService.listarProductos(
                nombre, categoriaId, minPrecio, maxPrecio, ordenar);
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto específico dado su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    // Obtener producto por ID (sin cambios)
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtener( @Parameter(description = "ID del producto", example = "1")
                                                            @PathVariable Long id) {
        ProductoResponseDTO dto = productoService.obtenerProducto(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Crear producto", description = "Registra un nuevo producto sin imagen. La imagen se asigna posteriormente mediante el endpoint /{id}/imagen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "La categoría especificada no existe")
    })

    // Crear producto – solo JSON (sin imagen)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        // No se recibe archivo, se crea el producto con imagenId = null
        ProductoResponseDTO creado = productoService.crearProducto(dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Subir imagen de producto", description = "Sube una imagen al microservicio ms-storage y la asocia al producto. La imagen debe enviarse como multipart/form-data con el campo 'archivo'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen asignada correctamente al producto"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con ms-storage"),
            @ApiResponse(responseCode = "503", description = "ms-storage no disponible")
    })


    // Subir imagen a un producto existente – multipart con solo archivo
    @PostMapping(path = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDTO> subirImagen(
            @Parameter(description = "ID del producto al que se asignará la imagen", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Archivo de imagen (jpg, png, webp)")
            @RequestPart("archivo") MultipartFile archivo) {
        ProductoResponseDTO actualizado = productoService.asignarImagen(id,archivo);
        return ResponseEntity.ok(actualizado);
    }


    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente. No modifica la imagen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })


    // Actualizar producto – solo JSON (sin imagen)
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @Parameter(description = "ID del producto a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO actualizado = productoService.actualizarProducto(id, dto, null);
        return ResponseEntity.ok(actualizado);
    }


    @Operation(summary = "Eliminar producto", description = "Realiza un soft delete del producto (lo marca como inactivo sin borrarlo de la base de datos)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })

    // Eliminar producto (soft delete, sin cambios)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", example = "1")
            @PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

}
