package com.duyhvt.spring_boot_library.config;

import com.okta.spring.boot.oauth.Okta;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable Cross Site Request Forgery (CSRF)
        http.csrf(AbstractHttpConfigurer::disable);

        // Protect endpoints at /api/<type>/secure
        http.authorizeHttpRequests(configurer ->
                configurer.requestMatchers(
                        // enable auth for mentioned routes matching with following pattern
                        "api/books/secure/**",
                                "/api/reviews/secure/**")
                        .authenticated()
                        // allow for rest of the routes without auth
                        .anyRequest().permitAll())
                // enables REST API support for JWT bearer tokens
                .oauth2ResourceServer(OAuthConfig ->
                        OAuthConfig.jwt(Customizer.withDefaults()));

        // Add CORS filters
        http.cors(cors -> {});

        // Add content negotiation strategy
        http.setSharedObject(ContentNegotiationStrategy.class,
                new HeaderContentNegotiationStrategy());

        // Force a non-empty response body for 401's to make the response friendly
        Okta.configureResourceServer401ResponseBody(http);

        return http.build();
    }
}
