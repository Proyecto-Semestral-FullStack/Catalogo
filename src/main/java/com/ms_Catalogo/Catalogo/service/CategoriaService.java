package com.ms_Catalogo.Catalogo.service;

import com.ms_Catalogo.Catalogo.dto.CategoriaRequestDTO;
import com.ms_Catalogo.Catalogo.dto.CategoriaResponseDTO;
import com.ms_Catalogo.Catalogo.exception.RecursoNoEncontradoException;
import com.ms_Catalogo.Catalogo.model.Categoria;
import com.ms_Catalogo.Catalogo.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    // Listar todas las categorías
    public List<CategoriaResponseDTO> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    // Obtener una categoría por ID
    public CategoriaResponseDTO obtenerCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));
        return convertirAResponseDTO(categoria);
    }

    // Crear una nueva categoría
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        Categoria guardada = categoriaRepository.save(categoria);
        return convertirAResponseDTO(guardada);
    }

    // Actualizar una categoría existente
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        Categoria actualizada = categoriaRepository.save(categoria);
        return convertirAResponseDTO(actualizada);
    }

    // Eliminar una categoría
    public void eliminarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));
        categoriaRepository.delete(categoria);
    }

    // Metodo auxiliar para convertir Categoría a DTO
    private CategoriaResponseDTO convertirAResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .build();
    }
}
