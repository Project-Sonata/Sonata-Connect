package com.odeyalo.sonata.connect.config;

import com.odeyalo.sonata.connect.service.player.BasicPlayerOperations;
import com.odeyalo.sonata.connect.service.player.DeviceOperations;
import com.odeyalo.sonata.connect.service.player.EventPublisherDeviceOperationsDecorator;
import com.odeyalo.sonata.connect.service.player.EventPublisherPlayerOperationsDecorator;
import com.odeyalo.sonata.connect.service.player.sync.PlayerSynchronizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PlayerOperationsConfiguration {

    @Bean
    @Primary
    public EventPublisherPlayerOperationsDecorator eventPublisherPlayerOperationsDecorator(BasicPlayerOperations delegate,
                                                                                           PlayerSynchronizationManager synchronizationManager) {
        return new EventPublisherPlayerOperationsDecorator(delegate, synchronizationManager);
    }

    @Bean
    @Primary
    public EventPublisherDeviceOperationsDecorator eventPublisherDeviceOperations(DeviceOperations delegate, PlayerSynchronizationManager synchronizationManager) {
        return new EventPublisherDeviceOperationsDecorator(delegate, synchronizationManager);
    }
}
