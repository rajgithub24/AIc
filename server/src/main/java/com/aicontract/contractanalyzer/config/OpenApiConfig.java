package com.aicontract.contractanalyzer.config;

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
    public OpenAPI contractAnalyzerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Contract Analyzer API")
                        .version("1.0.0")
                        .description("""
                                Backend APIs for PDF contract upload, async AI summarization,
                                risk clause extraction, compliance keyword detection,
                                contract search, filtering, and dashboard pagination.
                                """)
                        .contact(new Contact()
                                .name("AI Contract Analyzer"))
                        .license(new License()
                                .name("Portfolio Project")))
                .servers(List.of(new Server()
                        .url("http://localhost:8080")
                        .description("Local development server")));
    }
}
