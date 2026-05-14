-- ============================================
-- Script de población para ms-catalogo
-- Base de datos: db_catalogo
-- ============================================

-- 1. Insertar categorías
INSERT INTO categoria (nombre, descripcion) VALUES
('Videojuegos', 'Juegos para PC, consolas y ediciones especiales'),
('Consolas', 'Consolas de última generación y retro'),
('Coleccionables', 'Figuras, estatuas y ediciones limitadas'),
('Cómics', 'Cómics, novelas gráficas y mangas'),
('Merchandising', 'Ropa, tazas, pósters y accesorios');

-- 2. Guardar los IDs generados en variables (MySQL)
SET @cat_videojuegos = (SELECT id_categoria FROM categoria WHERE nombre = 'Videojuegos');
SET @cat_consolas = (SELECT id_categoria FROM categoria WHERE nombre = 'Consolas');
SET @cat_coleccionables = (SELECT id_categoria FROM categoria WHERE nombre = 'Coleccionables');
SET @cat_comics = (SELECT id_categoria FROM categoria WHERE nombre = 'Cómics');
SET @cat_merchandising = (SELECT id_categoria FROM categoria WHERE nombre = 'Merchandising');

-- 3. Insertar productos asociados a cada categoría
INSERT INTO producto (nombre_producto, descripcion, precio, categoria_id, imagen_id, activo) VALUES
('The Legend of Zelda: Tears of the Kingdom', 'Edición estándar para Nintendo Switch', 59990, @cat_videojuegos, NULL, 1),
('God of War Ragnarök', 'Edición para PS5', 49990, @cat_videojuegos, NULL, 1),
('PlayStation 5', 'Consola estándar con lector de discos', 599990, @cat_consolas, NULL, 1),
('Xbox Series X', 'Consola de alta potencia', 549990, @cat_consolas, NULL, 1),
('Figura Goku Ultra Instinct', 'Figura coleccionable de 25 cm', 39990, @cat_coleccionables, NULL, 1),
('Batman: El regreso del caballero oscuro', 'Novela gráfica tapa dura', 18990, @cat_comics, NULL, 1),
('Polera Friki "I paused my game to be here"', 'Polera de algodón, talla M', 12990, @cat_merchandising, NULL, 1);