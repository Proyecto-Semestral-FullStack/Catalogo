package com.ms_Catalogo.Catalogo.config;

import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms-Inventario API")
                        .version("1.0.0")
                        .description("Microservicio de gestión de stock e inventario para FrikiTienda")
                        .contact(new Contact()
                                .name("Equipo FrikiTienda")
                                .email("contacto@frikitienda.cl")));
    }
}
