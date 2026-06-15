package com.ms_Catalogo.Catalogo.service;

import com.ms_Catalogo.Catalogo.dto.ArchivoResponseDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoRequestDTO;
import com.ms_Catalogo.Catalogo.dto.ProductoResponseDTO;
import com.ms_Catalogo.Catalogo.exception.RecursoNoEncontradoException;
import com.ms_Catalogo.Catalogo.model.Categoria;
import com.ms_Catalogo.Catalogo.model.Producto;
import com.ms_Catalogo.Catalogo.repository.CategoriaRepository;
import com.ms_Catalogo.Catalogo.repository.ProductoRepository;
import com.ms_Catalogo.Catalogo.webclient.StorageClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private StorageClient storageClient;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void listarProductos_givenRepositoryReturnsProducts_mapsToDtoList() {
        // Given
        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Cat");

        Producto p = new Producto();
        p.setId(2L);
        p.setNombre("Prod");
        p.setDescripcion("Desc");
        p.setPrecio(BigDecimal.valueOf(9.99));
        p.setCategoria(cat);
        p.setImagenId(11L);
        p.setActivo(true);

        when(productoRepository.buscarConFiltros(null, null, null, null, null)).thenReturn(List.of(p));

        // When
        List<ProductoResponseDTO> resultado = productoService.listarProductos(null, null, null, null, null);

        // Then
        assertEquals(1, resultado.size());
        ProductoResponseDTO dto = resultado.get(0);
        assertEquals(2L, dto.getId());
        assertEquals("Cat", dto.getNombreCategoria());
        verify(productoRepository, times(1)).buscarConFiltros(null, null, null, null, null);
    }

    @Test
    void obtenerProducto_whenExists_returnsDto() {
        // Given
        Categoria cat = new Categoria();
        cat.setId(3L);
        cat.setNombre("C");
        Producto p = new Producto();
        p.setId(4L);
        p.setNombre("X");
        p.setCategoria(cat);
        p.setPrecio(BigDecimal.ONE);

        when(productoRepository.findById(4L)).thenReturn(Optional.of(p));

        // When
        ProductoResponseDTO dto = productoService.obtenerProducto(4L);

        // Then
        assertEquals(4L, dto.getId());
        assertEquals("C", dto.getNombreCategoria());
        verify(productoRepository, times(1)).findById(4L);
    }

    @Test
    void obtenerProducto_whenNotFound_throws() {
        // Given
        when(productoRepository.findById(100L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RecursoNoEncontradoException.class, () -> productoService.obtenerProducto(100L));
        verify(productoRepository, times(1)).findById(100L);
    }

    @Test
    void crearProducto_withoutFile_savesWithoutUploading() {
        // Given
        ProductoRequestDTO req = new ProductoRequestDTO();
        req.setNombre("P");
        req.setDescripcion("D");
        req.setPrecio(BigDecimal.TEN);
        req.setCategoriaId(7L);

        Categoria cat = new Categoria();
        cat.setId(7L);
        cat.setNombre("Cat7");

        when(categoriaRepository.findById(7L)).thenReturn(Optional.of(cat));

        Producto saved = new Producto();
        saved.setId(12L);
        saved.setNombre("P");
        saved.setDescripcion("D");
        saved.setPrecio(BigDecimal.TEN);
        saved.setCategoria(cat);
        saved.setActivo(true);

        when(productoRepository.save(any(Producto.class))).thenReturn(saved);

        // When
        ProductoResponseDTO dto = productoService.crearProducto(req, null);

        // Then
        assertEquals(12L, dto.getId());
        assertEquals("P", dto.getNombre());
        verify(categoriaRepository, times(1)).findById(7L);
        verify(storageClient, never()).uploadFile(any());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void crearProducto_withFile_uploadsImageAndSaves() {
        // Given
        ProductoRequestDTO req = new ProductoRequestDTO();
        req.setNombre("ImgProd");
        req.setDescripcion("D");
        req.setPrecio(BigDecimal.valueOf(5));
        req.setCategoriaId(8L);

        Categoria cat = new Categoria();
        cat.setId(8L);
        cat.setNombre("Cat8");

        when(categoriaRepository.findById(8L)).thenReturn(Optional.of(cat));

        MultipartFile archivo = mock(MultipartFile.class);
        when(archivo.isEmpty()).thenReturn(false);

        ArchivoResponseDTO ar = new ArchivoResponseDTO();
        ar.setId(99L);
        when(storageClient.uploadFile(archivo)).thenReturn(ar);

        Producto saved = new Producto();
        saved.setId(20L);
        saved.setNombre("ImgProd");
        saved.setCategoria(cat);
        saved.setPrecio(BigDecimal.valueOf(5));
        saved.setImagenId(99L);

        when(productoRepository.save(any(Producto.class))).thenReturn(saved);

        // When
        ProductoResponseDTO dto = productoService.crearProducto(req, archivo);

        // Then
        assertEquals(20L, dto.getId());
        assertEquals(99L, dto.getImagenId());
        verify(storageClient, times(1)).uploadFile(archivo);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void crearProducto_whenCategoriaNotFound_throws() {
        // Given
        ProductoRequestDTO req = new ProductoRequestDTO();
        req.setCategoriaId(999L);
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(RecursoNoEncontradoException.class, () -> productoService.crearProducto(req, null));
        verify(categoriaRepository, times(1)).findById(999L);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void crearProducto_withFile_whenStorageFails_throwsStorageException() {
        // Given
        ProductoRequestDTO req = new ProductoRequestDTO();
        req.setNombre("PErr");
        req.setDescripcion("D");
        req.setPrecio(BigDecimal.ONE);
        req.setCategoriaId(81L);

        Categoria cat = new Categoria();
        cat.setId(81L);
        cat.setNombre("Cat81");
        when(categoriaRepository.findById(81L)).thenReturn(Optional.of(cat));

        MultipartFile archivo = mock(MultipartFile.class);
        when(archivo.isEmpty()).thenReturn(false);

        // Simulamos que el StorageClient lanza StorageException
        when(storageClient.uploadFile(archivo)).thenThrow(new com.ms_Catalogo.Catalogo.exception.StorageException("Storage down"));

        // When / Then
        assertThrows(com.ms_Catalogo.Catalogo.exception.StorageException.class, () -> productoService.crearProducto(req, archivo));
        // No debe haberse guardado el producto
        verify(productoRepository, never()).save(any(Producto.class));
        verify(storageClient, times(1)).uploadFile(archivo);
    }

    @Test
    void actualizarProducto_withFile_updatesImage() {
        // Given
        Categoria catOld = new Categoria();
        catOld.setId(2L);
        catOld.setNombre("Old");

        Producto existing = new Producto();
        existing.setId(30L);
        existing.setNombre("X");
        existing.setCategoria(catOld);
        existing.setPrecio(BigDecimal.ONE);

        ProductoRequestDTO req = new ProductoRequestDTO();
        req.setNombre("New");
        req.setDescripcion("Nx");
        req.setPrecio(BigDecimal.valueOf(11));
        req.setCategoriaId(2L);

        when(productoRepository.findById(30L)).thenReturn(Optional.of(existing));
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(catOld));

        MultipartFile archivo = mock(MultipartFile.class);
        when(archivo.isEmpty()).thenReturn(false);

        ArchivoResponseDTO ar = new ArchivoResponseDTO();
        ar.setId(123L);
        when(storageClient.uploadFile(archivo)).thenReturn(ar);

        Producto saved = new Producto();
        saved.setId(30L);
        saved.setNombre("New");
        saved.setCategoria(catOld);
        saved.setPrecio(BigDecimal.valueOf(11));
        saved.setImagenId(123L);

        when(productoRepository.save(any(Producto.class))).thenReturn(saved);

        // When
        ProductoResponseDTO dto = productoService.actualizarProducto(30L, req, archivo);

        // Then
        assertEquals(30L, dto.getId());
        assertEquals(123L, dto.getImagenId());
        verify(storageClient, times(1)).uploadFile(archivo);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_whenProductNotFound_throws() {
        // Given
        when(productoRepository.findById(404L)).thenReturn(Optional.empty());
        ProductoRequestDTO req = new ProductoRequestDTO();
        req.setCategoriaId(1L);

        // When / Then
        assertThrows(RecursoNoEncontradoException.class, () -> productoService.actualizarProducto(404L, req, null));
        verify(productoRepository, times(1)).findById(404L);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminarProducto_setsActivoFalseAndSaves() {
        // Given
        Producto p = new Producto();
        p.setId(50L);
        p.setActivo(true);
        when(productoRepository.findById(50L)).thenReturn(Optional.of(p));
        when(productoRepository.save(any(Producto.class))).thenReturn(p);

        // When
        productoService.eliminarProducto(50L);

        // Then
        assertFalse(p.getActivo()); // after method activo debe ser false
        verify(productoRepository, times(1)).findById(50L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void asignarImagen_byId_assignsAndSaves() {
        // Given
        Producto p = new Producto();
        p.setId(60L);
        p.setImagenId(null);
        when(productoRepository.findById(60L)).thenReturn(Optional.of(p));
        when(productoRepository.save(any(Producto.class))).thenReturn(p);

        // When
        productoService.asignarImagen(60L, 77L);

        // Then
        assertEquals(77L, p.getImagenId());
        verify(productoRepository, times(1)).findById(60L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void asignarImagen_withFile_uploadsAndAssigns() {
        // Given
        Producto p = new Producto();
        p.setId(70L);
        // Añadimos una categoría al producto porque el método convertirADto accede a p.getCategoria().getNombre()
        // Si la categoría es null se produce NullPointerException durante la conversión a DTO.
        Categoria cat = new Categoria();
        cat.setId(77L);
        cat.setNombre("Cat70");
        p.setCategoria(cat);
        when(productoRepository.findById(70L)).thenReturn(Optional.of(p));

        MultipartFile archivo = mock(MultipartFile.class);
        ArchivoResponseDTO ar = new ArchivoResponseDTO();
        ar.setId(200L);
        when(storageClient.uploadFile(archivo)).thenReturn(ar);
        when(productoRepository.save(any(Producto.class))).thenReturn(p);

        // When
        ProductoResponseDTO dto = productoService.asignarImagen(70L, archivo);

        // Then
        assertEquals(200L, dto.getImagenId());
        verify(storageClient, times(1)).uploadFile(archivo);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void asignarImagen_withFile_whenStorageFails_throwsStorageException() {
        // Given
        Producto p = new Producto();
        p.setId(90L);
        Categoria cat = new Categoria();
        cat.setId(91L);
        cat.setNombre("Cat90");
        p.setCategoria(cat);
        when(productoRepository.findById(90L)).thenReturn(Optional.of(p));

        MultipartFile archivo = mock(MultipartFile.class);
        when(archivo.isEmpty()).thenReturn(false);

        when(storageClient.uploadFile(archivo)).thenThrow(new com.ms_Catalogo.Catalogo.exception.StorageException("Storage down"));

        // When / Then
        assertThrows(com.ms_Catalogo.Catalogo.exception.StorageException.class, () -> productoService.asignarImagen(90L, archivo));
        verify(storageClient, times(1)).uploadFile(archivo);
        verify(productoRepository, never()).save(any(Producto.class));
    }
}

