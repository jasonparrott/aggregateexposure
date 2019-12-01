package com.jasonparrott.aggregateexposure.position;

import com.jasonparrott.aggregateexposure.model.ProductType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PositionProducer {
    ProductType productType();
}
