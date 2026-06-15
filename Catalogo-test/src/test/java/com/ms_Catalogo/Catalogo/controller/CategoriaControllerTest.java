package com.ms_Catalogo.Catalogo.controller;

import com.ms_Catalogo.Catalogo.dto.CategoriaRequestDTO;
import com.ms_Catalogo.Catalogo.dto.CategoriaResponseDTO;
import com.ms_Catalogo.Catalogo.exception.RecursoNoEncontradoException;
import com.ms_Catalogo.Catalogo.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    // ==================== TESTS GET /api/categorias ====================

    @Test
    void listar_deberiaRetornarCategoriasConEstatus200() throws Exception {
        // Given - Preparar datos
        CategoriaResponseDTO cat1 = CategoriaResponseDTO.builder()
                .id(1L)
                .nombre("Electrónica")
                .descripcion("Productos electrónicos")
                .build();

        CategoriaResponseDTO cat2 = CategoriaResponseDTO.builder()
                .id(2L)
                .nombre("Libros")
                .descripcion("Libros y revistas")
                .build();

        when(categoriaService.listarCategorias()).thenReturn(List.of(cat1, cat2));

        // When & Then - Ejecutar y verificar
        mockMvc.perform(get("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Electrónica")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Libros")));

        verify(categoriaService, times(1)).listarCategorias();
    }

    @Test
    void listar_deberiaRetornarEstatus204CuandoNoHayCategorias() throws Exception {
        // Given - Lista vacía
        when(categoriaService.listarCategorias()).thenReturn(List.of());

        // When & Then - Verificar que devuelve 204 No Content
        mockMvc.perform(get("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoriaService, times(1)).listarCategorias();
    }

    // ==================== TESTS GET /api/categorias/{id} ====================

    @Test
    void obtener_deberiaRetornarCategoriaConEstatus200CuandoExiste() throws Exception {
        // Given - Categoría existente
        CategoriaResponseDTO categoria = CategoriaResponseDTO.builder()
                .id(5L)
                .nombre("Juguetes")
                .descripcion("Juguetes para niños")
                .build();

        when(categoriaService.obtenerCategoria(5L)).thenReturn(categoria);

        // When & Then
        mockMvc.perform(get("/api/categorias/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.nombre", is("Juguetes")))
                .andExpect(jsonPath("$.descripcion", is("Juguetes para niños")));

        verify(categoriaService, times(1)).obtenerCategoria(5L);
    }

    @Test
    void obtener_deberiaRetornarEstatus404CuandoNotFound() throws Exception {
        // Given - Categoría no existe
        when(categoriaService.obtenerCategoria(999L))
                .thenThrow(new RecursoNoEncontradoException("Categoría no encontrada con ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/categorias/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.mensaje", containsString("Categoría no encontrada")));

        verify(categoriaService, times(1)).obtenerCategoria(999L);
    }

    // ==================== TESTS POST /api/categorias ====================

    @Test
    void crear_deberiaRetornarEstatus201CuandoDatosValidos() throws Exception {
        // Given - Request válido
        String requestJson = """
                {
                    "nombre": "Nueva Categoría",
                    "descripcion": "Descripción de la nueva categoría"
                }
                """;

        CategoriaResponseDTO respuesta = CategoriaResponseDTO.builder()
                .id(10L)
                .nombre("Nueva Categoría")
                .descripcion("Descripción de la nueva categoría")
                .build();

        when(categoriaService.crearCategoria(any(CategoriaRequestDTO.class)))
                .thenReturn(respuesta);

        // When & Then
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nombre", is("Nueva Categoría")));

        verify(categoriaService, times(1)).crearCategoria(any(CategoriaRequestDTO.class));
    }

    @Test
    void crear_deberiaRetornarEstatus400CuandoNombreEnBlanco() throws Exception {
        // Given - Nombre vacío (falta validación)
        String requestJson = """
                {
                    "nombre": "",
                    "descripcion": "Descripción"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(categoriaService, never()).crearCategoria(any());
    }

    @Test
    void crear_deberiaRetornarEstatus400CuandoNombreExcede100Caracteres() throws Exception {
        // Given - Nombre muy largo
        String nombreLargo = "a".repeat(101);
        String requestJson = String.format("""
                {
                    "nombre": "%s",
                    "descripcion": "Descripción"
                }
                """, nombreLargo);

        // When & Then
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(categoriaService, never()).crearCategoria(any());
    }

    @Test
    void crear_deberiaRetornarEstatus400CuandoDescripcionExcede500Caracteres() throws Exception {
        // Given - Descripción muy larga
        String descripcionLarga = "a".repeat(501);
        String requestJson = String.format("""
                {
                    "nombre": "Categoría",
                    "descripcion": "%s"
                }
                """, descripcionLarga);

        // When & Then
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(categoriaService, never()).crearCategoria(any());
    }

    // ==================== TESTS PUT /api/categorias/{id} ====================

    @Test
    void actualizar_deberiaRetornarEstatus200CuandoExiste() throws Exception {
        // Given - Actualizar categoría existente
        String requestJson = """
                {
                    "nombre": "Categoría Actualizada",
                    "descripcion": "Nueva descripción"
                }
                """;

        CategoriaResponseDTO actualizada = CategoriaResponseDTO.builder()
                .id(3L)
                .nombre("Categoría Actualizada")
                .descripcion("Nueva descripción")
                .build();

        when(categoriaService.actualizarCategoria(eq(3L), any(CategoriaRequestDTO.class)))
                .thenReturn(actualizada);

        // When & Then
        mockMvc.perform(put("/api/categorias/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Categoría Actualizada")));

        verify(categoriaService, times(1)).actualizarCategoria(eq(3L), any(CategoriaRequestDTO.class));
    }

    @Test
    void actualizar_deberiaRetornarEstatus404CuandoNoExiste() throws Exception {
        // Given - Categoría no existe
        String requestJson = """
                {
                    "nombre": "Actualización",
                    "descripcion": "Descripción"
                }
                """;

        when(categoriaService.actualizarCategoria(eq(999L), any(CategoriaRequestDTO.class)))
                .thenThrow(new RecursoNoEncontradoException("Categoría no encontrada con ID: 999"));

        // When & Then
        mockMvc.perform(put("/api/categorias/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());

        verify(categoriaService, times(1)).actualizarCategoria(eq(999L), any(CategoriaRequestDTO.class));
    }

    // ==================== TESTS DELETE /api/categorias/{id} ====================

    @Test
    void eliminar_deberiaRetornarEstatus204CuandoExiste() throws Exception {
        // Given - Eliminar categoría existente
        doNothing().when(categoriaService).eliminarCategoria(7L);

        // When & Then
        mockMvc.perform(delete("/api/categorias/7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoriaService, times(1)).eliminarCategoria(7L);
    }

    @Test
    void eliminar_deberiaRetornarEstatus404CuandoNoExiste() throws Exception {
        // Given - Categoría no existe
        doThrow(new RecursoNoEncontradoException("Categoría no encontrada con ID: 888"))
                .when(categoriaService).eliminarCategoria(888L);

        // When & Then
        mockMvc.perform(delete("/api/categorias/888")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoriaService, times(1)).eliminarCategoria(888L);
    }
}