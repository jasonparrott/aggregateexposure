package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.generator.ValuationAgitator;
import com.jasonparrott.aggregateexposure.model.Client;
import com.jasonparrott.aggregateexposure.model.LinearSecurityGroup;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import com.jasonparrott.aggregateexposure.model.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class AggregateExposureApplication implements ApplicationRunner {
    private static LocalDate TODAY = LocalDate.of(2019, 11, 13);
    private static LocalDate PREV = LocalDate.of(2019, 11, 13);
    @Autowired
    public RiskEngine riskEngine;
    @Autowired
    public ValuationAgitator agitator;
    @Autowired
    public PortfolioBuilder portfolioBuilder;
    @Value("${clients:200}")
    private int clients;

    public static void main(String[] args) {
        SpringApplication.run(AggregateExposureApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Building clients...");
        for (int i = 0; i < clients; ++i) {
            Client c = new Client(i);
            Map<Integer, SecurityGroup> groupsById = new HashMap<>();
            List<Trade> trades = portfolioBuilder.getPortfolio(c);
            for (Trade trade : trades) {
                groupsById.putIfAbsent(
                        trade.getSecurityId(),
                        new LinearSecurityGroup(trade.getSecurityId(),
                                trade.getProductType(),
                                TODAY,
                                PREV));

                groupsById.get(trade.getSecurityId()).add(trade);
            }

            c.setTrades(groupsById.values().toArray(new SecurityGroup[groupsById.size()]));
            System.out.println(String.format("Adding client %d of %d to the engine.", i, clients));
            riskEngine.addClient(c);
        }

        System.out.println("Begining updates of market valuations.");
        agitator.start();
        System.out.println("App completed.");
    }
}