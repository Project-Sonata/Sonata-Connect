package testing.spring.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auto configure the SonataHttpOperations
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(SonataTestHttpOperationsAutoConfiguration.class)
public @interface AutoConfigureSonataHttpClient {
}
