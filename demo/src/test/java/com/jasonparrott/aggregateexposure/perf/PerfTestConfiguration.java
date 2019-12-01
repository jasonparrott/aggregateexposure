package com.jasonparrott.aggregateexposure.perf;

import com.jasonparrott.aggregateexposure.*;
import com.jasonparrott.aggregateexposure.generator.MarketValuationEngine;
import com.jasonparrott.aggregateexposure.generator.StaticGraphGenerator;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.GraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.SubgraphUpdateStrategy;
import com.jasonparrott.aggregateexposure.graph.mapper.AnnotationBasedValuationMapperFactory;
import com.jasonparrott.aggregateexposure.graph.mapper.ValuationMapperFactory;
import com.jasonparrott.aggregateexposure.metrics.AnnotationBasedMetricsCalculatorFactory;
import com.jasonparrott.aggregateexposure.metrics.MetricsCalculatorFactory;
import com.jasonparrott.aggregateexposure.partitioner.ClientIdPartitioner;
import com.jasonparrott.aggregateexposure.partitioner.Partitioner;
import com.jasonparrott.aggregateexposure.position.PositionService;
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

    @Value("${assets.min:500}")
    private int assertsPerClass;

    @Value("${assets.count:50}")
    private int securities;

    @Value("${clients:200}")
    private int clients;

    @Bean
    public RiskEngine riskEngine() throws ExportException, RiskCalculationException {
        return new RiskEngine(calculationGraph(),
                graphUpdateStrategy(),
                tradeUpdateManager(),
                valuationMapperFactory(),
                metricsCalculatorFactory(),
                partitioner(),
                tradeListener(),
                positionService(),
                updatePublisher());
    }

    @Bean
    public UpdatePublisher updatePublisher() {
        // null publisher
        return securityGroup -> {
        };
    }

    @Bean
    public PositionService positionService() {
        return new StaticPositionService();
    }

    @Bean
    public TradeListener tradeListener() throws RiskCalculationException, ExportException {
        return new OneShotTradeListener(clients, portfolioBuilder());
    }

    @Bean
    public BusinessDateService businessDateService() {
        return new StaticBusinessDateService();
    }

    @Bean
    public Partitioner partitioner() {
        return new ClientIdPartitioner(businessDateService());
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