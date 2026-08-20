package dev.pdrolcs.autenticacao.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Autenticacao API")
                        .version("1.0.0")
                        .description("API de autenticação e autorização usando Spring Boot, JWT e Spring Security")
                        .contact(new Contact()
                                .name("Pedro Lucas")
                                .email("plfm3k@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor local")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub")
                        .url("https://github.com/pdroLcs/autenticacao"));
    }

}
