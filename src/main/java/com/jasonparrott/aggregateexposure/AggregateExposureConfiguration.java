package com.jasonparrott.aggregateexposure;

import com.google.common.collect.ImmutableList;
import com.jasonparrott.aggregateexposure.generator.FakePortfolioGenerator;
import com.jasonparrott.aggregateexposure.generator.MarketValuationEngine;
import com.jasonparrott.aggregateexposure.generator.RiskGraphGenerator;
import com.jasonparrott.aggregateexposure.generator.ValuationAgitator;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.GraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.SubgraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.mapper.RandomBondTradeValuationMapper;
import com.jasonparrott.aggregateexposure.graph.mapper.RandomSwapTradeValuationMapper;
import com.jasonparrott.aggregateexposure.graph.mapper.TradeValuationMapper;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AggregateExposureConfiguration {

    @Value("${valuations:1000}")
    private int valuations;
    @Value("${assets.min:1000}")
    private int minPerAssetClass;
    @Value("${assets.max:5000}")
    private int maxPerAssetClass;

    @Value("${iterations:10000}")
    private int iterations;

    @Bean
    public RiskEngine riskEngine() {
        return new RiskEngine(pricingGraph(), graphUpdateStrategy(), valuationMappers(), tradeUpdateManager());
    }

    @Bean
    public ValuationAgitator valuationAgitator() {
        return new ValuationAgitator(iterations, valuationEngine(), riskEngine());
    }

    @Bean
    public PortfolioBuilder portfolioBuilder() {
        return new FakePortfolioGenerator(pricingGraph(), minPerAssetClass, maxPerAssetClass);
    }

    private Collection<TradeValuationMapper> valuationMappers() {
        return ImmutableList.of(
                new RandomSwapTradeValuationMapper(),
                new RandomBondTradeValuationMapper());
    }

    private GraphUpdateStrategy graphUpdateStrategy() {
        return new SubgraphUpdateStrategy(executorService());
    }

    private ExecutorService executorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    private MarketValuationEngine valuationEngine() {
        return new MarketValuationEngine(valuations);
    }

    private Graph<CalculationNode, DefaultEdge> pricingGraph() {
        return RiskGraphGenerator.generateGraph(valuationEngine().getAllValuations(), 0.25d, tradeUpdateManager());
    }

    private TradeUpdateManager tradeUpdateManager() {
        return new DelegatingExecutorTradeUpdateManager(executorService());
    }


}
