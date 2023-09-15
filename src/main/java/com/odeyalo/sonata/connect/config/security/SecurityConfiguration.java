package com.odeyalo.sonata.connect.config.security;

import com.odeyalo.sonata.connect.config.security.configurer.HttpRequestServerWebExchangeMatcher;
import com.odeyalo.sonata.connect.config.security.configurer.WebSocketServerWebExchangeMatcher;
import com.odeyalo.suite.security.annotation.EnableSuiteSecurity;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableSuiteSecurity
@EnableWebFluxSecurity
@Builder
public class SecurityConfiguration {
    @Autowired
    Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> authorizeExchangeSpecCustomizer;
    @Autowired
    Customizer<ServerHttpSecurity.ExceptionHandlingSpec> exceptionHandlingSpecCustomizer;
    @Autowired
    Customizer<ServerHttpSecurity.CorsSpec> corsSpecCustomizer;
    @Autowired
    Customizer<ServerHttpSecurity.CsrfSpec> csrfSpecCustomizer;
    @Autowired
    AuthenticationWebFilter authenticationManagerFilter;
    @Autowired
    HttpRequestServerWebExchangeMatcher httpRequestServerWebExchangeMatcher;
    @Autowired
    WebSocketServerWebExchangeMatcher webSocketServerWebExchangeMatcher;

    @Bean
    public SecurityWebFilterChain defaultFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .securityMatcher(httpRequestServerWebExchangeMatcher)
                .authorizeExchange(authorizeExchangeSpecCustomizer)
                .cors(corsSpecCustomizer)
                .csrf(csrfSpecCustomizer)
                .exceptionHandling(exceptionHandlingSpecCustomizer)
                .addFilterAt(authenticationManagerFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public SecurityWebFilterChain webSocketFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .securityMatcher(webSocketServerWebExchangeMatcher)
                .addFilterAfter(authenticationManagerFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(authorizeExchangeSpecCustomizer)
                .cors(corsSpecCustomizer)
                .csrf(csrfSpecCustomizer)
                .exceptionHandling(exceptionHandlingSpecCustomizer)
                .build();
    }


}

