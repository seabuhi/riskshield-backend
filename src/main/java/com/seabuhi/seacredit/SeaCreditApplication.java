package com.seabuhi.seacredit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SeaCreditApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeaCreditApplication.class, args);
        System.out.println("""
                
                ╔══════════════════════════════════════════════════════╗
                ║          Sea-Credit Enterprise API Started          ║
                ║                                                      ║
                ║  Swagger UI  →  http://localhost:8080/swagger-ui.html║
                ║  API Docs    →  http://localhost:8080/v3/api-docs    ║
                ╚══════════════════════════════════════════════════════╝
                """);
    }
}


