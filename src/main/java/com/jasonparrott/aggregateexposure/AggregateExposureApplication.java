package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.generator.ValuationAgitator;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class AggregateExposureApplication implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(AggregateExposureApplication.class, args);
    }

    @Autowired
    public RiskEngine riskEngine;

    @Autowired
    public ValuationAgitator agitator;

    @Autowired
    public PortfolioBuilder portfolioBuilder;

    @Value("${clients:1000}")
    private int clients;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Building clients...");
        for (int i = 0; i < clients; ++i) {
            Client c = new Client(i);
            List<Trade> trades = portfolioBuilder.getPortfolio(c);
            c.setTrades(trades.toArray(new Trade[trades.size()]));
            System.out.println(String.format("Adding client %d of %d to the engine.", i, clients));
            riskEngine.addClient(c);
        }

        System.out.println("Begining updates of market valuations.");
        agitator.start();
        System.out.println("App completed.");
    }
}