package com.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Jbase API")
                .description("Documentação da API genérica do sistema Jbase")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Michel Pech")
                    .email("michel@gpro.com")
                    .url("https://devstraub.github.io/myPage/"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://springdoc.org")));
    }
}
