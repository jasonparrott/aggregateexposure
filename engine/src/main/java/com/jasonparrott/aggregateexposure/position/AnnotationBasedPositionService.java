package com.jasonparrott.aggregateexposure.position;

import com.jasonparrott.aggregateexposure.model.ProductType;
import com.jasonparrott.aggregateexposure.model.position.Position;
import com.jasonparrott.aggregateexposure.model.trade.Trade;
import io.github.classgraph.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AnnotationBasedPositionService implements PositionService {
    private final Map<ProductType, PositionService> factoryMap = new HashMap<>();

    public AnnotationBasedPositionService() {
        this(null);
    }

    public AnnotationBasedPositionService(String basePackage) {
        if (StringUtils.isEmpty(basePackage)) {
            basePackage = getClass().getPackage().getName();
        }

        try (ScanResult result = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .whitelistPackages(basePackage)
                .scan()) {

            ClassInfoList classInfos = result.getClassesWithAnnotation(PositionProducer.class.getName());
            classInfos.forEach(ci -> {
                try {
                    PositionService service = (PositionService) ci.loadClass().newInstance();
                    AnnotationParameterValueList params = ci.getAnnotationInfo(PositionProducer.class.getName()).getParameterValues();
                    AnnotationEnumValue prodTypeEnumValue = (AnnotationEnumValue) params.getValue("productType");
                    ProductType productType = (ProductType) prodTypeEnumValue.loadClassAndReturnEnumValue();
                    factoryMap.put(productType, service);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public Position getPositionForTrade(Trade trade) {
        return factoryMap.get(trade.getProductType()).getPositionForTrade(trade);
    }
}
