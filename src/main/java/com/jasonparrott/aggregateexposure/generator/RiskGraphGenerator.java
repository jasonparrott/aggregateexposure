package com.jasonparrott.aggregateexposure.generator;

import com.jasonparrott.aggregateexposure.SecurityGroupUpdateManager;
import com.jasonparrott.aggregateexposure.graph.CalculationNode;
import com.jasonparrott.aggregateexposure.graph.IntermediateCalculator;
import com.jasonparrott.aggregateexposure.graph.MarketValuationCalculator;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.IntStream;

public class RiskGraphGenerator {

    /**
     * Generates a directed acyclic pricing graph
     * @param valuations - valuations which will be updated by the market
     * @param freeCalculatorPercentage - percentage of "free" nodes to insert randomly between valuations.
     * @return
     */
    public static Graph<CalculationNode, DefaultEdge> generateGraph(Collection<MarketValuation> valuations, double freeCalculatorPercentage, SecurityGroupUpdateManager securityGroupUpdateManager) throws ExportException {
        if (freeCalculatorPercentage < 0.0d)
            throw new IllegalArgumentException("free calculator percent must be between 0.0 and 1.0");
        if (freeCalculatorPercentage > 1.0d)
            throw new IllegalArgumentException("free calculator percent must be between 0.0 and 1.0");
        if (valuations == null)
            throw new IllegalArgumentException("must provide valuations");

        Graph<CalculationNode, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        System.out.println("Generating pricing graph.");
        List<CalculationNode> nodes = new LinkedList<>();
        valuations.forEach(v -> nodes.add(new CalculationNode(new MarketValuationCalculator(v, securityGroupUpdateManager))));
        for(int i = 0; i < valuations.size() * freeCalculatorPercentage; ++i) {
            //calculators.add(new IntermediateCalculator(Collections.emptyList()));
            nodes.add(new CalculationNode(new IntermediateCalculator(new LinkedList<>(), securityGroupUpdateManager, UUID.randomUUID().toString())));
        }
        System.out.println("Adding verticies to graph.");
        nodes.forEach(graph::addVertex);

        // implemented from https://stackoverflow.com/a/12792416
        System.out.println("Calculating acyclic tree.");
        Random r = new Random();
        int n = nodes.size() * (nodes.size() - 1) / 2;
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
        for (int i = 0; i < nodes.size(); ++i) {
            endpoints.add(i);
        }

        Collections.shuffle(endpoints);

        for(Integer s : sample) {
            int tailIndex = (int) (0.5 + Math.sqrt((s + 1) * 2));
            int headIndex = s - tailIndex * (tailIndex - 1) / 2;

            CalculationNode head = nodes.get(headIndex);
            CalculationNode tail = nodes.get(tailIndex);

            if (tail.getCalculator() instanceof IntermediateCalculator && head.getCalculator() != null) {
                ((IntermediateCalculator) tail.getCalculator()).addInput(head.getCalculator());
            }

            try {
                graph.addEdge(head, tail);
            } catch (IllegalArgumentException iae) {
                // skip
            }
        }
        System.out.println("Finished constructing graph.");

        ComponentNameProvider<CalculationNode> vertexIdProvider = new ComponentNameProvider<CalculationNode>() {
            int i = 0;

            public String getName(CalculationNode node) {
                return String.valueOf(i);
            }
        };
        ComponentNameProvider<CalculationNode> vertexLabelProvider = new ComponentNameProvider<CalculationNode>() {
            public String getName(CalculationNode node) {
                if (node.getCalculator() instanceof IntermediateCalculator) {
                    return "IM: " + node.getCalculator().getInputs().size();
                } else {
                    MarketValuationCalculator calculator = (MarketValuationCalculator) node.getCalculator();

                    return String.format("MarketValue: ");
                }

            }
        };

        Writer writer = new StringWriter();
        GraphExporter<CalculationNode, DefaultEdge> exporter =
                new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
        exporter.exportGraph(graph, writer);
        System.out.println(writer.toString());

        return graph;
    }

}
