package DinicAlgoritmStructures;

import GraphStructures.Graph;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DinicAlgorithm {
    Map<String, Double> distance;
    Map<String, Integer> ptr;
    Map<String, List<FlowEdge>> flowGraph;
    Graph initialGraph;

    public DinicAlgorithm(Graph graph){
        this.distance = new HashMap<>();
        this.ptr = new HashMap<>();
        this.flowGraph = graph.createFlowsGraph();
        this.initialGraph = graph;
    }

    public double dinic(String startVertex, String secondVertex) {
        Double flow = 0.0;
        for (; ; ) {
            if (!dinicHelpBfs(startVertex, secondVertex)) break;
            initialGraph.getAdjacencyList().keySet().forEach(vertex -> ptr.put(vertex, 0));
            Double pushed = -1.0;
            while (pushed != 0.0) {
                pushed = dinicHelpDfs(startVertex, Double.MAX_VALUE, secondVertex);
                if (pushed == 0.0) break;
                flow += pushed;
            }
        }
        return flow;
    }

    private boolean dinicHelpBfs(String startVertex, String secondVertex) {
        initialGraph.getAdjacencyList().keySet().forEach(vertex -> distance.put(vertex, -1.0));
        distance.put(startVertex, 0.0);
        Map<Integer, String> queue = new HashMap<>();
        Integer qSize = 0;
        queue.put(qSize++, startVertex);
        for (int i = 0; i < qSize; i++) {
            String vertex = queue.get(i);
            for (FlowEdge edge : flowGraph.get(vertex))
                if (distance.get(edge.getTargerVertex()) < 0 && edge.getFlow() < edge.getValue()) {
                    distance.put(edge.getTargerVertex(), distance.get(vertex) + 1);
                    queue.put(qSize++, edge.getTargerVertex());
                }
        }
        Double d = distance.get(startVertex);
        return distance.get(secondVertex) >= 0;
    }

    private double dinicHelpDfs(String startVertex, Double flow, String secondVertex) {
        if (startVertex.equals(secondVertex))
            return flow;
        for (; ptr.get(startVertex) < flowGraph.get(startVertex).size(); ptr.put(startVertex, ptr.get(startVertex) + 1)) {
            FlowEdge edge = flowGraph.get(startVertex).get(ptr.get(startVertex));
            if (distance.get(edge.getTargerVertex()) == distance.get(startVertex) + 1 && edge.getFlow() < edge.getValue()) {
                Double df = dinicHelpDfs(edge.getTargerVertex(), Math.min(flow, edge.getValue() - edge.getFlow()), secondVertex);
                if (df > 0) {
                    edge.setFlow(edge.getFlow() + df);
                    flowGraph.get(edge.getTargerVertex())
                            .get(edge.getReversedVertex())
                            .setFlow(flowGraph.get(edge.getTargerVertex()).get(edge.getReversedVertex()).getFlow() - df);
                    return df;
                }
            }
        }
        return 0.0;
    }
}
