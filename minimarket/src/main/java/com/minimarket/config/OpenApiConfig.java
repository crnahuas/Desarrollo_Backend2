package com.minimarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI minimarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Minimarket Plus API")
                        .version("1.0.0")
                        .description("Documentacion OpenAPI de los endpoints REST para productos, carrito, inventario, ventas, categorias y usuarios del backend Minimarket Plus.")
                        .license(new License().name("Uso academico Duoc UC")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Entorno local de desarrollo")
                ));
    }
}
