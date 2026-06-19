package com.ms_Catalogo.Catalogo.service;

import com.ms_Catalogo.Catalogo.dto.CategoriaRequestDTO;
import com.ms_Catalogo.Catalogo.dto.CategoriaResponseDTO;
import com.ms_Catalogo.Catalogo.exception.RecursoNoEncontradoException;
import com.ms_Catalogo.Catalogo.model.Categoria;
import com.ms_Catalogo.Catalogo.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void listarCategorias_givenRepositoryHasCategorias_returnsListOfResponseDTOs() {
        // Given: preparar datos y mocks
        Categoria c1 = new Categoria();
        c1.setId(1L);
        c1.setNombre("Cat A");
        c1.setDescripcion("Desc A");

        Categoria c2 = new Categoria();
        c2.setId(2L);
        c2.setNombre("Cat B");
        c2.setDescripcion("Desc B");

        when(categoriaRepository.findAll()).thenReturn(List.of(c1, c2));

        // When: ejecutar el método a probar
        List<CategoriaResponseDTO> resultado = categoriaService.listarCategorias();

        // Then: validaciones
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(d -> d.getId().equals(1L) && d.getNombre().equals("Cat A")));
        assertTrue(resultado.stream().anyMatch(d -> d.getId().equals(2L) && d.getNombre().equals("Cat B")));
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void obtenerCategoria_whenExists_returnsResponseDTO() {
        // Given
        Categoria c = new Categoria();
        c.setId(10L);
        c.setNombre("Electrónica");
        c.setDescripcion("Productos eléctricos");

        when(categoriaRepository.findById(10L)).thenReturn(Optional.of(c));

        // When
        CategoriaResponseDTO dto = categoriaService.obtenerCategoria(10L);

        // Then
        assertEquals(10L, dto.getId());
        assertEquals("Electrónica", dto.getNombre());
        verify(categoriaRepository, times(1)).findById(10L);
    }

    @Test
    void obtenerCategoria_whenNotFound_throwsRecursoNoEncontradoException() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.obtenerCategoria(99L));
        verify(categoriaRepository, times(1)).findById(99L);
    }

    @Test
    void crearCategoria_givenRequest_savesAndReturnsDto() {
        // Given
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre("Nuevo");
        request.setDescripcion("Desc");

        Categoria guardada = new Categoria();
        guardada.setId(5L);
        guardada.setNombre("Nuevo");
        guardada.setDescripcion("Desc");

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(guardada);

        // When
        CategoriaResponseDTO respuesta = categoriaService.crearCategoria(request);

        // Then
        assertEquals(5L, respuesta.getId());
        assertEquals("Nuevo", respuesta.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_whenExists_updatesAndReturnsDto() {
        // Given
        Categoria existente = new Categoria();
        existente.setId(7L);
        existente.setNombre("Old");
        existente.setDescripcion("OldDesc");

        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre("Updated");
        request.setDescripcion("NewDesc");

        Categoria guardada = new Categoria();
        guardada.setId(7L);
        guardada.setNombre("Updated");
        guardada.setDescripcion("NewDesc");

        when(categoriaRepository.findById(7L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(guardada);

        // When
        CategoriaResponseDTO resultado = categoriaService.actualizarCategoria(7L, request);

        // Then
        assertEquals(7L, resultado.getId());
        assertEquals("Updated", resultado.getNombre());
        verify(categoriaRepository, times(1)).findById(7L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void actualizarCategoria_whenNotFound_throws() {
        // Given
        when(categoriaRepository.findById(100L)).thenReturn(Optional.empty());
        CategoriaRequestDTO req = new CategoriaRequestDTO();
        req.setNombre("X");

        // When / Then
        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.actualizarCategoria(100L, req));
        verify(categoriaRepository, times(1)).findById(100L);
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void eliminarCategoria_whenExists_deletes() {
        // Given
        Categoria c = new Categoria();
        c.setId(3L);
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(c));

        // When
        categoriaService.eliminarCategoria(3L);

        // Then
        verify(categoriaRepository, times(1)).findById(3L);
        verify(categoriaRepository, times(1)).delete(c);
    }

    @Test
    void eliminarCategoria_whenNotFound_throws() {
        // Given
        when(categoriaRepository.findById(55L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.eliminarCategoria(55L));
        verify(categoriaRepository, times(1)).findById(55L);
        verify(categoriaRepository, never()).delete(any());
    }
}

