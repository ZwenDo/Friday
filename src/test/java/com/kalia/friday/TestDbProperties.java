package com.kalia.friday;

import io.micronaut.context.annotation.Property;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test specific annotation to easily apply database properties to set up a temporary database.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Documented
@Property(name = "datasources.default.url", value = "jdbc:h2:mem:test")
@Property(name = "datasources.default.driverClassName", value = "org.h2.Driver")
@Property(name = "jpa.default.properties.hibernate.hbm2ddl.auto", value = "update")
public @interface TestDbProperties {
}
