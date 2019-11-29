package com.jasonparrott.aggregateexposure;

import com.google.common.collect.ImmutableList;
import com.jasonparrott.aggregateexposure.calculators.product.MetricsMapper;
import com.jasonparrott.aggregateexposure.calculators.product.StaticMetricsMapper;
import com.jasonparrott.aggregateexposure.generator.FakePortfolioGenerator;
import com.jasonparrott.aggregateexposure.generator.MarketValuationEngine;
import com.jasonparrott.aggregateexposure.generator.StaticGraphGenerator;
import com.jasonparrott.aggregateexposure.generator.ValuationAgitator;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.GraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.SubgraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.mapper.RandomBondTradeValuationMapper;
import com.jasonparrott.aggregateexposure.graph.mapper.RandomSwapTradeValuationMapper;
import com.jasonparrott.aggregateexposure.graph.mapper.SecurityGroupValuationMapper;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ExportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AggregateExposureConfiguration {

    @Value("${valuations:8}")
    private int valuations;
    @Value("${assets.min:1000}")
    private int minPerAssetClass;
    @Value("${assets.max:5000}")
    private int maxPerAssetClass;
    @Value("${assets.count:50}")
    private int securities;

    @Value("${iterations:10000}")
    private int iterations;

    @Bean
    public RiskEngine riskEngine() throws ExportException {
        return new RiskEngine(pricingGraph(), graphUpdateStrategy(), valuationMappers(), tradeUpdateManager(), metricsMapper());
    }

    @Bean
    public ValuationAgitator valuationAgitator() throws ExportException {
        return new ValuationAgitator(iterations, valuationEngine(), executorService(), riskEngine());
    }

    @Bean
    public PortfolioBuilder portfolioBuilder() throws ExportException {
        return new FakePortfolioGenerator(pricingGraph(), minPerAssetClass, maxPerAssetClass, securities);
    }

    @Bean
    public Collection<SecurityGroupValuationMapper> valuationMappers() {
        return ImmutableList.of(
                new RandomSwapTradeValuationMapper(),
                new RandomBondTradeValuationMapper());
    }

    @Bean
    public GraphUpdateStrategy graphUpdateStrategy() {
        return new SubgraphUpdateStrategy(executorService());
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Bean
    public MarketValuationEngine valuationEngine() {
        return new MarketValuationEngine(valuations);
    }

    @Bean
    public Graph<CalculationNode, DefaultEdge> pricingGraph() throws ExportException {
        //return RiskGraphGenerator.generateGraph(valuationEngine().getAllValuations(), 0.50d, tradeUpdateManager());
        return StaticGraphGenerator.generateGraph(valuationEngine().getAllValuations(), tradeUpdateManager());
    }

    @Bean
    public SecurityGroupUpdateManager tradeUpdateManager() {
        return new DelegatingExecutorSecurityGroupUpdateManager(executorService());
    }

    @Bean
    public MetricsMapper metricsMapper() {
        return new StaticMetricsMapper();
    }
}
