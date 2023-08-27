package com.odeyalo.sonata.connect.service.support.mapper;

/**
 * Mapper to convert one object to another
 * @param <T> - first type
 * @param <R> - second type
 */
public interface Converter<T, R> {
    /**
     * Convert T to R and return it
     * @param t - type to convert from
     * @return  - converted value
     */
    R convertTo(T t);
}
