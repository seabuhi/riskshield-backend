package com.seabuhi.riskshield.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI riskshieldOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RiskShield Enterprise API")
                        .description("""
                                **RiskShield** — Enterprise-grade banking backend with 10 API modules:
                                
                                | # | Module | Base URL |
                                |---|--------|----------|
                                | 1 | Auth | `/auth/**` |
                                | 2 | OTP | `/otp/**` |
                                | 3 | Notification | `/api/v1/notifications/**` |
                                | 4 | Credit Scoring | `/api/v1/credit-scoring/**` |
                                | 5 | Fraud Detection | `/api/v1/fraud/**` |
                                | 6 | Audit Log | `/api/v1/audit-logs/**` |
                                | 7 | File Upload | `/api/v1/documents/**` |
                                | 8 | Admin Panel | `/api/v1/admin/**` |
                                | 9 | Rate Limiting | Built-in filter |
                                | 10 | Analytics | `/api/v1/analytics/**` |
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("RiskShield Team")
                                .email("dev@riskshield.az")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("BearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}



