package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.Calculator;
import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import io.github.classgraph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnnotationBasedMetricsCalculatorFactory implements MetricsCalculatorFactory {
    private final Map<ProductType, MetricsCalculatorFactory> factoryMap = new HashMap<>();

    public AnnotationBasedMetricsCalculatorFactory() {
        this(null);
    }

    public AnnotationBasedMetricsCalculatorFactory(String basePackage) {
        if (StringUtils.isEmpty(basePackage)) {
            basePackage = getClass().getPackage().getName();
        }

        try (ScanResult result = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .whitelistPackages(basePackage)
                .scan()) {

            ClassInfoList classInfos = result.getClassesWithAnnotation(MetricsCalculatorProducer.class.getName());
            classInfos.stream().forEach(ci -> {
                try {
                    MetricsCalculatorFactory factory = (MetricsCalculatorFactory) ci.loadClass().newInstance();
                    AnnotationParameterValueList params = ci.getAnnotationInfo(MetricsCalculatorProducer.class.getName()).getParameterValues();
                    AnnotationEnumValue prodTypeEnumValue = (AnnotationEnumValue) params.getValue("productType");
                    ProductType productType = (ProductType) prodTypeEnumValue.loadClassAndReturnEnumValue();
                    factoryMap.put(productType, factory);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public MetricsCalculator forSecurityGroup(SecurityGroup securityGroup, Collection<Calculator> inputs) throws Exception {
        return factoryMap.get(securityGroup.getProductType()).forSecurityGroup(securityGroup, inputs);
    }
}
