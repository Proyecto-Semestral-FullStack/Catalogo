package com.ms_Catalogo.Catalogo.controller;

import com.ms_Catalogo.Catalogo.dto.ProductoRequestDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoResponseDTO;
import com.ms_Catalogo.Catalogo.exception.RecursoNoEncontradoException;
import com.ms_Catalogo.Catalogo.exception.StorageException;
import com.ms_Catalogo.Catalogo.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    // ==================== TESTS GET /api/productos ====================

    @Test
    void listar_deberiaRetornarProductosConEstatus200() throws Exception {
        // Given - Preparar lista de productos
        ProductoResponseDTO prod1 = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Laptop gaming")
                .precio(BigDecimal.valueOf(1200.00))
                .nombreCategoria("Electrónica")
                .imagenId(100L)
                .activo(true)
                .build();

        ProductoResponseDTO prod2 = ProductoResponseDTO.builder()
                .id(2L)
                .nombre("Mouse")
                .descripcion("Mouse inalámbrico")
                .precio(BigDecimal.valueOf(25.00))
                .nombreCategoria("Accesorios")
                .imagenId(101L)
                .activo(true)
                .build();

        when(productoService.listarProductos(null, null, null, null, null))
                .thenReturn(List.of(prod1, prod2));

        // When & Then
        mockMvc.perform(get("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Laptop")))
                .andExpect(jsonPath("$[1].precio", is(25.00)));

        verify(productoService, times(1)).listarProductos(null, null, null, null, null);
    }

    @Test
    void listar_deberiaRetornarEstatus204CuandoNoHayProductos() throws Exception {
        // Given
        when(productoService.listarProductos(null, null, null, null, null))
                .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).listarProductos(null, null, null, null, null);
    }

    @Test
    void listar_deberiaAplicarFiltros() throws Exception {
        // Given - Filtros aplicados
        ProductoResponseDTO prod = ProductoResponseDTO.builder()
                .id(5L)
                .nombre("Monitor")
                .precio(BigDecimal.valueOf(300.00))
                .nombreCategoria("Electrónica")
                .activo(true)
                .build();

        when(productoService.listarProductos("Monitor", 1L, BigDecimal.valueOf(200),
                BigDecimal.valueOf(500), "precio"))
                .thenReturn(List.of(prod));

        // When & Then
        mockMvc.perform(get("/api/productos")
                        .param("nombre", "Monitor")
                        .param("categoriaId", "1")
                        .param("minPrecio", "200")
                        .param("maxPrecio", "500")
                        .param("ordenar", "precio")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Monitor")));

        verify(productoService, times(1)).listarProductos("Monitor", 1L,
                BigDecimal.valueOf(200), BigDecimal.valueOf(500), "precio");
    }

    @Test
    void listar_deberiaRetornarErrorCuandoMinPrecioInvalido() throws Exception {
        // Given - Parámetro inválido (no es número)
        // When & Then - MethodArgumentTypeMismatchException
        mockMvc.perform(get("/api/productos")
                        .param("minPrecio", "abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).listarProductos(any(), any(), any(), any(), any());
    }

    // ==================== TESTS GET /api/productos/{id} ====================

    @Test
    void obtener_deberiaRetornarProductoConEstatus200() throws Exception {
        // Given
        ProductoResponseDTO producto = ProductoResponseDTO.builder()
                .id(10L)
                .nombre("Teclado")
                .descripcion("Teclado mecánico RGB")
                .precio(BigDecimal.valueOf(150.00))
                .nombreCategoria("Accesorios")
                .imagenId(105L)
                .activo(true)
                .build();

        when(productoService.obtenerProducto(10L)).thenReturn(producto);

        // When & Then
        mockMvc.perform(get("/api/productos/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nombre", is("Teclado")))
                .andExpect(jsonPath("$.precio", is(150.00)));

        verify(productoService, times(1)).obtenerProducto(10L);
    }

    @Test
    void obtener_deberiaRetornarEstatus404CuandoNotFound() throws Exception {
        // Given
        when(productoService.obtenerProducto(999L))
                .thenThrow(new RecursoNoEncontradoException("Producto con ID 999 no encontrado"));

        // When & Then
        mockMvc.perform(get("/api/productos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        verify(productoService, times(1)).obtenerProducto(999L);
    }

    // ==================== TESTS POST /api/productos (JSON) ====================

    @Test
    void crear_deberiaRetornarEstatus201ConDatosValidos() throws Exception {
        // Given - Request válido (JSON sin imagen)
        String requestJson = """
                {
                    "nombre": "Monitor 4K",
                    "descripcion": "Monitor 4K 32 pulgadas",
                    "precio": 450.50,
                    "categoriaId": 1
                }
                """;

        ProductoResponseDTO creado = ProductoResponseDTO.builder()
                .id(15L)
                .nombre("Monitor 4K")
                .descripcion("Monitor 4K 32 pulgadas")
                .precio(BigDecimal.valueOf(450.50))
                .nombreCategoria("Electrónica")
                .imagenId(null)
                .activo(true)
                .build();

        when(productoService.crearProducto(any(ProductoRequestDTO.class), eq(null)))
                .thenReturn(creado);

        // When & Then
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(15)))
                .andExpect(jsonPath("$.nombre", is("Monitor 4K")))
                .andExpect(jsonPath("$.imagenId", nullValue()));

        verify(productoService, times(1)).crearProducto(any(ProductoRequestDTO.class), eq(null));
    }

    @Test
    void crear_deberiaRetornarEstatus400CuandoNombreEnBlanco() throws Exception {
        // Given
        String requestJson = """
                {
                    "nombre": "",
                    "descripcion": "Descripción",
                    "precio": 100.00,
                    "categoriaId": 1
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).crearProducto(any(), any());
    }

    @Test
    void crear_deberiaRetornarEstatus400CuandoPrecio0() throws Exception {
        // Given - Precio <= 0
        String requestJson = """
                {
                    "nombre": "Producto",
                    "descripcion": "Descripción",
                    "precio": 0,
                    "categoriaId": 1
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(productoService, never()).crearProducto(any(), any());
    }

    @Test
    void crear_deberiaRetornarEstatus404CuandoCategoriaNoExiste() throws Exception {
        // Given
        String requestJson = """
                {
                    "nombre": "Producto",
                    "descripcion": "Descripción",
                    "precio": 100.00,
                    "categoriaId": 999
                }
                """;

        when(productoService.crearProducto(any(ProductoRequestDTO.class), eq(null)))
                .thenThrow(new RecursoNoEncontradoException("Categoría con ID 999 no existe"));

        // When & Then
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        verify(productoService, times(1)).crearProducto(any(ProductoRequestDTO.class), eq(null));
    }

    // ==================== TESTS POST /api/productos/{id}/imagen (multipart) ====================

    @Test
    void subirImagen_deberiaRetornarEstatus200ConArchivoValido() throws Exception {
        // Given - Archivo válido
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "imagen.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "contenido de la imagen".getBytes()
        );

        ProductoResponseDTO actualizado = ProductoResponseDTO.builder()
                .id(20L)
                .nombre("Producto con imagen")
                .precio(BigDecimal.valueOf(100.00))
                .nombreCategoria("Categoría")
                .imagenId(200L)
                .activo(true)
                .build();

        when(productoService.asignarImagen(eq(20L), any()))
                .thenReturn(actualizado);

        // When & Then
        mockMvc.perform(multipart("/api/productos/20/imagen")
                        .file(archivo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(20)))
                .andExpect(jsonPath("$.imagenId", is(200)));

        verify(productoService, times(1)).asignarImagen(eq(20L), any());
    }

    @Test
    void subirImagen_deberiaRetornarEstatus502CuandoStorageFalla() throws Exception {
        // Given - Storage falla
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "imagen.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "contenido".getBytes()
        );

        when(productoService.asignarImagen(eq(21L), any()))
                .thenThrow(new StorageException("No se pudo subir la imagen. El servicio de almacenamiento no está disponible"));

        // When & Then
        mockMvc.perform(multipart("/api/productos/21/imagen")
                        .file(archivo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status", is(502)));

        verify(productoService, times(1)).asignarImagen(eq(21L), any());
    }

    @Test
    void subirImagen_deberiaRetornarEstatus404CuandoProductoNotFound() throws Exception {
        // Given
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "imagen.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "contenido".getBytes()
        );

        when(productoService.asignarImagen(eq(999L), any()))
                .thenThrow(new RecursoNoEncontradoException("Producto no encontrado"));

        // When & Then
        mockMvc.perform(multipart("/api/productos/999/imagen")
                        .file(archivo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());

        verify(productoService, times(1)).asignarImagen(eq(999L), any());
    }

    // ==================== TESTS PUT /api/productos/{id} (JSON) ====================

    @Test
    void actualizar_deberiaRetornarEstatus200ConDatosValidos() throws Exception {
        // Given
        String requestJson = """
                {
                    "nombre": "Producto Actualizado",
                    "descripcion": "Descripción actualizada",
                    "precio": 199.99,
                    "categoriaId": 1
                }
                """;

        ProductoResponseDTO actualizado = ProductoResponseDTO.builder()
                .id(30L)
                .nombre("Producto Actualizado")
                .descripcion("Descripción actualizada")
                .precio(BigDecimal.valueOf(199.99))
                .nombreCategoria("Electrónica")
                .activo(true)
                .build();

        when(productoService.actualizarProducto(eq(30L), any(ProductoRequestDTO.class), eq(null)))
                .thenReturn(actualizado);

        // When & Then
        mockMvc.perform(put("/api/productos/30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(30)))
                .andExpect(jsonPath("$.nombre", is("Producto Actualizado")));

        verify(productoService, times(1)).actualizarProducto(eq(30L), any(ProductoRequestDTO.class), eq(null));
    }

    @Test
    void actualizar_deberiaRetornarEstatus404CuandoNotFound() throws Exception {
        // Given
        String requestJson = """
                {
                    "nombre": "Producto",
                    "descripcion": "Descripción",
                    "precio": 100.00,
                    "categoriaId": 1
                }
                """;

        when(productoService.actualizarProducto(eq(999L), any(ProductoRequestDTO.class), eq(null)))
                .thenThrow(new RecursoNoEncontradoException("Producto con ID 999 no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/productos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());

        verify(productoService, times(1)).actualizarProducto(eq(999L), any(ProductoRequestDTO.class), eq(null));
    }

    // ==================== TESTS DELETE /api/productos/{id} ====================

    @Test
    void eliminar_deberiaRetornarEstatus204CuandoExiste() throws Exception {
        // Given
        doNothing().when(productoService).eliminarProducto(40L);

        // When & Then
        mockMvc.perform(delete("/api/productos/40")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).eliminarProducto(40L);
    }

    @Test
    void eliminar_deberiaRetornarEstatus404CuandoNotFound() throws Exception {
        // Given
        doThrow(new RecursoNoEncontradoException("Producto con ID 999 no encontrado"))
                .when(productoService).eliminarProducto(999L);

        // When & Then
        mockMvc.perform(delete("/api/productos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productoService, times(1)).eliminarProducto(999L);
    }
}