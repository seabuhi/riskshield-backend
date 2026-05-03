package com.seabuhi.riskshield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class riskshieldApplication {

    public static void main(String[] args) {
        SpringApplication.run(riskshieldApplication.class, args);
        System.out.println("""
                
                ╔══════════════════════════════════════════════════════╗
                ║          RiskShield Enterprise API Started          ║
                ║                                                      ║
                ║  Swagger UI  →  http://localhost:8080/swagger-ui.html║
                ║  API Docs    →  http://localhost:8080/v3/api-docs    ║
                ╚══════════════════════════════════════════════════════╝
                """);
    }
}



