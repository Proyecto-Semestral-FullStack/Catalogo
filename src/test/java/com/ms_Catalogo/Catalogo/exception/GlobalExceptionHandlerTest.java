package com.ms_Catalogo.Catalogo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // ==================== TESTS RecursoNoEncontradoException ====================

    @Test
    void manejarRecursoNoEncontrado_deberiaRetornarEstatus404() throws Exception {
        // When - Llamar endpoint que lanza RecursoNoEncontradoException
        // Then - Debe retornar 404 con ErrorResponse
        mockMvc.perform(get("/test/recurso-no-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.mensaje", containsString("Recurso")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    // ==================== TESTS MethodArgumentTypeMismatchException ====================

    @Test
    void manejarErrorDeTipo_deberiaRetornarEstatus400CuandoTipoIncorrecto() throws Exception {
        // When - Parámetro con tipo incorrecto
        // Then - Debe retornar 400 Bad Request
        mockMvc.perform(get("/test/tipo-invalido")
                        .param("precio", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.mensaje", containsString("Valor inválido")));
    }

    // ==================== TESTS StorageException ====================

    @Test
    void manejarErrorStorage_deberiaRetornarEstatus502() throws Exception {
        // When - Lanza StorageException
        // Then - Debe retornar 502 Bad Gateway
        mockMvc.perform(get("/test/storage-error"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status", is(502)))
                .andExpect(jsonPath("$.mensaje", containsString("Storage")));
    }

    // ==================== TESTS IllegalStateException ====================

    @Test
    void manejarErrorDeNegocio_deberiaRetornarEstatus409Conflict() throws Exception {
        // When - Lanza IllegalStateException
        // Then - Debe retornar 409 Conflict
        mockMvc.perform(get("/test/negocio-error"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    // ==================== TESTS Exception Genérica ====================

    @Test
    void manejarErrorGeneral_deberiaRetornarEstatus500() throws Exception {
        // When - Lanza Exception genérica
        // Then - Debe retornar 500 Internal Server Error
        mockMvc.perform(get("/test/error-generico"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.mensaje", containsString("Error interno")));
    }

    // ==================== TEST CONTROLLER para simular excepciones ====================

    @RestController
    public static class TestExceptionController {

        @GetMapping("/test/recurso-no-encontrado")
        public void testRecursoNoEncontrado() {
            throw new RecursoNoEncontradoException("Recurso no encontrado");
        }

        @GetMapping("/test/tipo-invalido")
        public void testTipoInvalido(@RequestParam Long precio) {
            // El error ocurre automáticamente si no es Long
        }

        @GetMapping("/test/storage-error")
        public void testStorageError() {
            throw new StorageException("Error en servicio de storage");
        }

        @GetMapping("/test/negocio-error")
        public void testNegocioError() {
            throw new IllegalStateException("Violación de regla de negocio");
        }

        @GetMapping("/test/error-generico")
        public void testErrorGenerico() {
            throw new RuntimeException("Error no controlado");
        }
    }
}