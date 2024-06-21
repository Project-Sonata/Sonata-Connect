package com.odeyalo.sonata.connect.config.factory;

import com.odeyalo.sonata.connect.entity.DeviceEntity;
import com.odeyalo.sonata.connect.entity.TrackItemEntity;
import com.odeyalo.sonata.connect.entity.factory.DeviceEntityFactory;
import com.odeyalo.sonata.connect.entity.factory.PlayableItemEntityFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfiguration {

    @Bean
    public DeviceEntityFactory deviceEntityFactory() {
        return new DeviceEntity.Factory();
    }

    @Bean
    public PlayableItemEntityFactory playableItemEntityFactory() {
        return new TrackItemEntity.Factory();
    }
}
