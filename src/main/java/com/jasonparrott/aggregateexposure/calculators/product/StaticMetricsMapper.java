package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.ProductType;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class StaticMetricsMapper implements MetricsMapper {

    @Override
    public MetricsCalculator getMetrics(ProductType productType, List<CalculationNode> inputs) {
        switch (productType) {
            case Swap:
                return new SwapMetricsCalculator(inputs.stream().map(i -> i.getCalculator()).collect(toList()));
            case Bond:
                return new BondMetricsCalculator(inputs.stream().map(i -> i.getCalculator()).collect(toList()));
            default:
                throw new IllegalArgumentException("Unknown product type.");
        }
    }
}
