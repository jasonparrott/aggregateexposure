package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.model.ProductType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MetricsCalculatorProducer {
    ProductType productType();
}
