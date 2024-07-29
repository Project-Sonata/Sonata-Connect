package com.odeyalo.sonata.connect.support.web.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom annotation that used to annotate the parameters of type {@link com.odeyalo.sonata.connect.model.Device}
 * to parse request body by {@link com.odeyalo.sonata.connect.support.web.resolver.DeviceResolver}.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ConnectionTarget {
}
