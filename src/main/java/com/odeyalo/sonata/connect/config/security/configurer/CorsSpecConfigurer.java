package com.odeyalo.sonata.connect.config.security.configurer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Used to configure the {@link CorsSpec}
 */
@Component
public class CorsSpecConfigurer implements Customizer<CorsSpec> {

    @Override
    public void customize(CorsSpec corsSpec) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", config);

        corsSpec.configurationSource(source);
    }
}
