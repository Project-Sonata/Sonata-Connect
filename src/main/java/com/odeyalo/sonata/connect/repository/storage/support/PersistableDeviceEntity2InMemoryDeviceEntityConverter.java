package com.odeyalo.sonata.connect.repository.storage.support;

import com.odeyalo.sonata.connect.entity.InMemoryDeviceEntity;
import com.odeyalo.sonata.connect.repository.storage.PersistableDeviceEntity;
import org.springframework.stereotype.Component;

/**
 * Convert PersistableDeviceEntity to InMemoryDeviceEntity
 */
@Component
public class PersistableDeviceEntity2InMemoryDeviceEntityConverter implements PersistableEntityConverter<PersistableDeviceEntity, InMemoryDeviceEntity> {

    @Override
    public InMemoryDeviceEntity convertTo(PersistableDeviceEntity type) {
        return InMemoryDeviceEntity.copy(type);
    }

    @Override
    public PersistableDeviceEntity convertFrom(InMemoryDeviceEntity type) {
        return PersistableDeviceEntity.copy(type);
    }
}
