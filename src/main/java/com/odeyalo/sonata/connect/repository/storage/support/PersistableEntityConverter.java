package com.odeyalo.sonata.connect.repository.storage.support;

/**
 * Interface to convert the entity from one type to another.
 * @param <T> - type to convert
 * @param <R> - conversion result
 */
public interface PersistableEntityConverter<T, R> {

    R convertTo(T type);

    T convertFrom(R type);

}
