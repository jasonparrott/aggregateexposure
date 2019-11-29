package com.jasonparrott.aggregateexposure.perf;

import com.jasonparrott.aggregateexposure.DelegatingExecutorSecurityGroupUpdateManager;
import com.jasonparrott.aggregateexposure.PortfolioBuilder;
import com.jasonparrott.aggregateexposure.RiskEngine;
import com.jasonparrott.aggregateexposure.SecurityGroupUpdateManager;
import com.jasonparrott.aggregateexposure.calculators.product.AnnotationBasedMetricsCalculatorFactory;
import com.jasonparrott.aggregateexposure.calculators.product.MetricsCalculatorFactory;
import com.jasonparrott.aggregateexposure.generator.MarketValuationEngine;
import com.jasonparrott.aggregateexposure.generator.StaticGraphGenerator;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.GraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.SubgraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.mapper.AnnotationBasedValuationMapperFactory;
import com.jasonparrott.aggregateexposure.graph.mapper.ValuationMapperFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.ExportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
//@Profile("perftest")
//@ActiveProfiles("perftest")
public class PerfTestConfiguration {

    @Value("${valuations:8}")
    private int valuations;
    @Value("${assets.min:5000}")
    private int assertsPerClass;
    @Value("${assets.count:50}")
    private int securities;

    @Value("${iterations:1000000000}")
    private int iterations;

    @Bean
    public RiskEngine riskEngine() throws ExportException {
        return new RiskEngine(calculationGraph(), graphUpdateStrategy(), tradeUpdateManager(), valuationMapperFactory(), metricsCalculatorFactory());
    }

    @Bean
    public PortfolioBuilder portfolioBuilder() throws ExportException {
        return new StaticPortfolioGenerator(calculationGraph(), assertsPerClass, securities);
    }

    @Bean
    public ValuationMapperFactory valuationMapperFactory() {
        return new AnnotationBasedValuationMapperFactory("com.jasonparrott.aggregateexposure.perf.mapper");
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
    public Graph<CalculationNode, DefaultEdge> calculationGraph() throws ExportException {
        return StaticGraphGenerator.generateGraph(valuationEngine().getAllValuations(), tradeUpdateManager());
    }

    @Bean
    public SecurityGroupUpdateManager tradeUpdateManager() {
        return new DelegatingExecutorSecurityGroupUpdateManager(executorService());
    }

    @Bean
    public MetricsCalculatorFactory metricsCalculatorFactory() {
        return new AnnotationBasedMetricsCalculatorFactory();
    }
}