package com.jasonparrott.aggregateexposure.graph;

import com.google.common.collect.ImmutableSet;
import com.jasonparrott.aggregateexposure.model.MarketValuation;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SubgraphUpdateStrategy implements GraphUpdateStrategy {
    private final ExecutorService executorService;

    public SubgraphUpdateStrategy(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public Future<?> update(final Graph<CalculationNode, DefaultEdge> graph, Set<CalculationNode> leaves, final MarketValuation valuation) {
        return executorService.submit(() -> {
            DirectedAcyclicGraph<CalculationNode, DefaultEdge> subgraph;

            Optional<CalculationNode> calcNode = graph.vertexSet().stream().filter(v -> v.equals(new MarketValuationCalculator(valuation, null))).findAny();
            if (!calcNode.isPresent())
                throw new NoSuchElementException("Could not find node in vertex set.");

            if (calcNode.get().getSubgraph() != null) {
                // already computed, just use the cached one
                subgraph = (DirectedAcyclicGraph<CalculationNode, DefaultEdge>) calcNode.get().getSubgraph();
            } else {
                // find all the paths in the subtree starting from the updated node
                AllDirectedPaths<CalculationNode, DefaultEdge> allDirectedPaths = new AllDirectedPaths<>(graph);
                List<GraphPath<CalculationNode, DefaultEdge>> pathList = allDirectedPaths.getAllPaths(
                        ImmutableSet.of(valuationNode(valuation)),
                        leaves,
                        true,
                        Integer.MAX_VALUE);
                // build a subgraph of said nodes and edges. This guarantees we only traverse these nodes/edges once as duplicates are not
                // put in the graph
                subgraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
                for (GraphPath<CalculationNode, DefaultEdge> path : pathList) {
                    CalculationNode start = null;
                    CalculationNode end = null;
                    for (CalculationNode node : path.getVertexList()) {
                        start = end;
                        end = node;

                        // we'll always have an end
                        subgraph.addVertex(end);
                        if (start != null) {
                            subgraph.addVertex(start);
                            subgraph.addEdge(start, end);
                        }
                    }
                }

                calcNode.get().setSubgraph(subgraph);
            }
            // top down (breadth first) traversal calculating each node
            // this ensures we calc and changed inputs before the each node iteself
            BreadthFirstIterator<CalculationNode, DefaultEdge> iterator = new BreadthFirstIterator<>(subgraph);
            while (iterator.hasNext()) {
                CalculationNode node = iterator.next();
                node.lock();
                node.getCalculator().calculate();
                node.unlock();
            }

        });
    }

    private CalculationNode valuationNode(MarketValuation valuation) {
        return new CalculationNode(new MarketValuationCalculator(valuation, null));
    }
}
