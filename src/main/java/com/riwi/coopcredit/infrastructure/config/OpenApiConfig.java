package com.riwi.coopcredit.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration for CoopCredit API
 * 
 * Configures the API documentation with:
 * - JWT Bearer authentication
 * - API metadata and contact information
 * - Server information
 * - Security schemes
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";
        
        return new OpenAPI()
                // Add security requirement to all endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                
                // Configure security schemes
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer token obtained from /auth/login endpoint. " +
                                                "Include in Authorization header: Bearer <token>")
                        )
                )
                
                // Add server information
                .addServersItem(new Server()
                        .url("http://localhost:8081")
                        .description("Local Development Server")
                )
                .addServersItem(new Server()
                        .url("https://api.coopcredit.com")
                        .description("Production Server")
                )
                
                // API Information
                .info(new Info()
                        .title("CoopCredit - Credit Application Management API")
                        .version("1.0.0")
                        .description("Comprehensive credit application management system for cooperatives. " +
                                "Built with Hexagonal Architecture, JWT Security, Advanced Validations, " +
                                "and Credit Risk Evaluation.\n\n" +
                                "**Key Features:**\n" +
                                "- User authentication and authorization with JWT\n" +
                                "- Affiliate (cooperative member) management\n" +
                                "- Credit application creation and evaluation\n" +
                                "- Automatic risk assessment\n" +
                                "- Advanced input validation\n" +
                                "- Global error handling\n" +
                                "- Comprehensive API documentation\n\n" +
                                "**Getting Started:**\n" +
                                "1. Register a new user: POST /auth/register\n" +
                                "2. Login: POST /auth/login\n" +
                                "3. Use the returned token in the Authorization header\n" +
                                "4. Access protected endpoints with your token\n\n" +
                                "**Documentation:**\n" +
                                "- Full API documentation available at /v3/api-docs\n" +
                                "- Interactive Swagger UI at /swagger-ui/index.html")
                        .termsOfService("https://coopcredit.com/terms")
                        .contact(new Contact()
                                .name("CoopCredit Development Team")
                                .email("support@coopcredit.com")
                                .url("https://coopcredit.com")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                );
    }
}