package com.duyhvt.spring_boot_library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringCorsConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("Loaded SpringCorsConfiguration --->");
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "https://localhost:3000")
                .allowedHeaders("*")
                .allowedMethods("*");
    }
}
