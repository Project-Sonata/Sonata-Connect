package com.odeyalo.sonata.connect.config;

import com.odeyalo.sonata.common.context.ContextUriParser;
import com.odeyalo.sonata.common.context.HardcodedContextUriParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config to create beans related for context-uri
 */
@Configuration
public class ContextUriBeansConfiguration {

    @Bean
    public ContextUriParser contextUriParser() {
        return new HardcodedContextUriParser();
    }
}
