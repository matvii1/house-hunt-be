package com.house.hunter.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("House Hunter API")
                        .version("1.0")
                        .description("House Hunter API Documentation")
                        .contact(new Contact()
                                .name("House Hunter")
                                .email("burakugar77@gmail.com")));

    }
}

