package com.mdev.ferreteria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración centralizada de CORS.
 * <p>
 * Los orígenes permitidos NO se hardcodean acá: se leen desde
 * {@code app.cors.allowed-origins} (application.properties / variables de
 * entorno), para que el mismo build sirva para cualquier ambiente
 * (local, staging, producción) sin recompilar — solo cambiando esa
 * propiedad al desplegar.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
