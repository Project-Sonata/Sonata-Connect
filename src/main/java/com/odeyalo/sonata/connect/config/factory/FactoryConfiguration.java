package com.odeyalo.sonata.connect.config.factory;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.factory.DeviceEntityFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfiguration {

    @Bean
    public DeviceEntityFactory deviceEntityFactory() {
        return new DeviceEntity.Factory();
    }

}
