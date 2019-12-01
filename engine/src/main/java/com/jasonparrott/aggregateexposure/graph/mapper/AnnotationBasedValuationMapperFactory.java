package com.jasonparrott.aggregateexposure.graph.mapper;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.SecurityGroupId;
import io.github.classgraph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AnnotationBasedValuationMapperFactory implements ValuationMapperFactory {
    private final Map<ProductType, ValuationMapperFactory> factoryMap = new HashMap<>();

    public AnnotationBasedValuationMapperFactory() {
        this(null);
    }

    public AnnotationBasedValuationMapperFactory(String basePachkage) {
        if (StringUtils.isEmpty(basePachkage))
            basePachkage = getClass().getPackage().getName();

        try (ScanResult result = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .whitelistPackages(basePachkage)
                .scan()) {

            ClassInfoList classInfos = result.getClassesWithAnnotation(ValuationMapperProducer.class.getName());
            classInfos.stream().forEach(ci -> {
                try {
                    ValuationMapperFactory factory = (ValuationMapperFactory) ci.loadClass().newInstance();
                    AnnotationParameterValueList params = ci.getAnnotationInfo(ValuationMapperProducer.class.getName()).getParameterValues();
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
    public SecurityGroupValuationMapper forSecurityGroup(SecurityGroupId securityGroup) throws Exception {
        return factoryMap.get(securityGroup.getProductType()).forSecurityGroup(securityGroup);
    }
}
