package com.ms_Catalogo.Catalogo.config;

import com.ms_Catalogo.Catalogo.model.Categoria;
import com.ms_Catalogo.Catalogo.model.Producto;
import com.ms_Catalogo.Catalogo.repository.CategoriaRepository;
import com.ms_Catalogo.Catalogo.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Evitar inserción duplicada
        if (categoriaRepository.count() > 0) {
            log.info("Los datos ya existen. Se omite la inicialización.");
            return;
        }

        log.info(">>> Iniciando carga de datos de prueba...");

        // 1. Crear categorías
        Categoria videojuegos = categoriaRepository.save(
                new Categoria(null, "Videojuegos", "Juegos para PC, consolas y ediciones especiales", null));
        Categoria consolas = categoriaRepository.save(
                new Categoria(null, "Consolas", "Consolas de última generación y retro", null));
        Categoria coleccionables = categoriaRepository.save(
                new Categoria(null, "Coleccionables", "Figuras, estatuas y ediciones limitadas", null));
        Categoria comics = categoriaRepository.save(
                new Categoria(null, "Cómics", "Cómics, novelas gráficas y mangas", null));
        Categoria merchandising = categoriaRepository.save(
                new Categoria(null, "Merchandising", "Ropa, tazas, pósters y accesorios", null));

        // 2. Insertar productos
        productoRepository.save(new Producto(null,
                "The Legend of Zelda: Tears of the Kingdom",
                "Edición estándar para Nintendo Switch",
                new BigDecimal("59990"),
                videojuegos, null, true));

        productoRepository.save(new Producto(null,
                "God of War Ragnarök",
                "Edición para PS5",
                new BigDecimal("49990"),
                videojuegos, null, true));

        productoRepository.save(new Producto(null,
                "PlayStation 5",
                "Consola estándar con lector de discos",
                new BigDecimal("599990"),
                consolas, null, true));

        productoRepository.save(new Producto(null,
                "Xbox Series X",
                "Consola de alta potencia",
                new BigDecimal("549990"),
                consolas, null, true));

        productoRepository.save(new Producto(null,
                "Figura Goku Ultra Instinct",
                "Figura coleccionable de 25 cm",
                new BigDecimal("39990"),
                coleccionables, null, true));

        productoRepository.save(new Producto(null,
                "Batman: El regreso del caballero oscuro",
                "Novela gráfica tapa dura",
                new BigDecimal("18990"),
                comics, null, true));

        productoRepository.save(new Producto(null,
                "Polera Friki \"I paused my game to be here\"",
                "Polera de algodón, talla M",
                new BigDecimal("12990"),
                merchandising, null, true));

        log.info(">>> Datos de prueba insertados: 5 categorías, 7 productos.");
    }

}
