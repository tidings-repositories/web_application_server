package com.delivalue.tidings.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class WebConfig {

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        List<String> origins = new ArrayList<>(List.of(
                "https://stellagram.kr",
                "https://www.stellagram.kr"
        ));
        if ("dev".equals(activeProfile) || "local".equals(activeProfile)) {
            origins.add("https://dev.stellagram.kr");
            origins.add("http://localhost:5173");
        }

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(origins.toArray(String[]::new))
                        .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS");
            }
        };
    }
}
