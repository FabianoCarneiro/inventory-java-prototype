package com.example.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração do Swagger/OpenAPI.
 * Define o título, versão e descrição da documentação automática da API de inventário.
 *
 * @author Fabiano Carneiro
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Inventory API")
                .version("1.0")
                .description("API para controle de estoque e reservas"));
    }
}