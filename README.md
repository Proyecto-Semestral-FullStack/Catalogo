# 🛒 ms-Catalogo – FrikiTienda

Microservicio de catálogo de productos. Gestiona productos, categorías y sus relaciones. Proporciona filtros combinados, ordenación y está preparado para integrarse con `ms-storage` para la gestión de imágenes.

[![Java 21](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)

---

## 📡 Comunicación entre microservicios

| Relación | Microservicio | Implementación |
|----------|---------------|----------------|
| **Consume** | `ms-storage` | WebClient (subida de imágenes, pendiente de activar) |
| **Es consumido por** | `ms-inventario`, `ms-carrito`, `ms-pedido`, `ms-reseñas` | WebClient (validación de producto, obtención de nombre/precio) |

---


## 🌐 Endpoints REST

### Productos

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/productos` | Listar productos (filtros opcionales) |
| `GET` | `/api/productos/{id}` | Obtener producto por ID |
| `POST` | `/api/productos` | Crear un nuevo producto |
| `PUT` | `/api/productos/{id}` | Actualizar un producto existente |
| `DELETE` | `/api/productos/{id}` | Desactivar producto (soft delete) |
| `POST` | `/api/productos/{id}/imagen` | Asignar imagen al producto (pendiente de activar) |

---

## 🛠️ Requisitos y configuración

1. **Base de datos:** crear manualmente la BD `db_catalogo` en MySQL (XAMPP).
2. **Configurar `application.properties`:**
   ```properties
   server.port=8082
   spring.datasource.url=jdbc:mysql://localhost:3306/db_catalogo
   spring.datasource.username=root
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   # URL del servicio de storage (cuando esté disponible)
   storage.service.url=http://localhost:8088

   Ejecutar: mvn spring-boot:run o desde IntelliJ.

Probar: usar Postman en http://localhost:8082/api/productos.

📂 Repositorio y rama
Rama principal: main

Rama de desarrollo: feature/ms-catalogo

Clonar: git clone https://github.com/tu-usuario/ms-catalogo.git

<img width="2303" height="1075" alt="deepseek_mermaid_20260514_d29792" src="https://github.com/user-attachments/assets/32ee3599-b61b-4cee-812d-d81d394d2283" />


Las URLs de otros servicios se configuran en application.properties.
