package com.seabuhi.seacredit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Feature toggles — enable/disable modules without code changes.
 * Control via application.yml or environment variables.
 */
@Configuration
@ConfigurationProperties(prefix = "app.features")
@Data
public class FeatureToggle {
    private boolean fraudDetection = true;
    private boolean emailNotifications = true;
    private boolean rateLimiting = true;
    private boolean auditLogging = true;
    private boolean caching = true;
}


