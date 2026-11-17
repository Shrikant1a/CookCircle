package com.cookcircle.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cookCircleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CookCircle REST API")
                        .description("Housing Society Shared Cooking Management Platform — REST API Documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CookCircle Engineering Team")
                                .email("engineering@cookcircle.com")
                                .url("https://cookcircle.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://cookcircle.com/terms")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
