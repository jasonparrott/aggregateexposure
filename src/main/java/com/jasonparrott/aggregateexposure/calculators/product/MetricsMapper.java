package com.jasonparrott.aggregateexposure.calculators.product;

import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.model.ProductType;

import java.util.List;

public interface MetricsMapper {
    MetricsCalculator getMetrics(ProductType productType, List<CalculationNode> inputs);
}
