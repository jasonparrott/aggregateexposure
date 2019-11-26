package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.calculators.graph.Calculator;
import com.jasonparrott.aggregateexposure.calculators.graph.IntermediateCalculator;
import com.jasonparrott.aggregateexposure.calculators.graph.MarketValuationCalculator;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class RiskGraphGenerator {

    /**
     * Generates a directed acyclic pricing graph
     * @param valuations - valuations which will be updated by the market
     * @param freeCalculatorPercentage - percentage of "free" nodes to insert randomly between valuations.
     * @return
     */
    public static Graph<Calculator, DefaultEdge> generateGraph(Collection<MarketValuation> valuations, double freeCalculatorPercentage)
    {
        if (freeCalculatorPercentage < 0.0d)
            throw new IllegalArgumentException("free calculator percent must be between 0.0 and 1.0");
        if (freeCalculatorPercentage > 1.0d)
            throw new IllegalArgumentException("free calculator percent must be between 0.0 and 1.0");
        if (valuations == null)
            throw new IllegalArgumentException("must provide valuations");

        Graph<Calculator, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        System.out.println("Generating pricing graph.");
        List<Calculator> calculators = valuations.stream().map(MarketValuationCalculator::new).collect(toList());
        for(int i = 0; i < valuations.size() * freeCalculatorPercentage; ++i) {
            calculators.add(new IntermediateCalculator());
        }
        System.out.println("Adding verticies to graph.");
        calculators.stream().forEach(c-> graph.addVertex(c));

        // implemented from https://stackoverflow.com/a/12792416

        System.out.println("Calculating acyclic tree.");
        Random r = new Random();
        int n = calculators.size() * (calculators.size() - 1) / 2;
        int e = n/2; // edges
        IntStream ints = r.ints(0, n - e );
        List<Integer> sample = new LinkedList<>();
        PrimitiveIterator.OfInt iterator = ints.iterator();
        for(int i = 0; i < e && iterator.hasNext(); ++i) {
            sample.add(iterator.nextInt());
        }
        Collections.sort(sample);
        for(int i = 0; i < e; ++i) {
            sample.set(i, sample.get(i) + i);
        }

        List<Integer> endpoints = new LinkedList<>();
        for(int i = 0; i < calculators.size(); ++i) {
            endpoints.add(i);
        }

        Collections.shuffle(endpoints);

        for(Integer s : sample) {
            int tailIndex = (int) (0.5 + Math.sqrt((s + 1) * 2));
            int headIndex = s - tailIndex * (tailIndex - 1) / 2;

            Calculator head = calculators.get(headIndex);
            Calculator tail = calculators.get(tailIndex);

            graph.addEdge(head, tail);
        }
        System.out.println("Finished constructing graph.");
        return graph;
    }

}
