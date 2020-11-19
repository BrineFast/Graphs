package BoruvkaAlgorithmStructures;

import GraphStructures.Graph;
import GraphStructures.Pair;
import lombok.Data;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MinimalSpanningTree {
    private Graph minimalSpanningTree = new Graph(false, new HashMap<>());
    public int treeWeight;
    private Endpoint[] closestEdgeArray;

    public MinimalSpanningTree(Graph initialGraph) {
        if (!initialGraph.isConnected()) {
            System.out.println("Graph is not connected");
            return;
        }
        Map<String, Map<String, Integer>> graph = initialGraph.getAdjacencyList();
        List<Endpoint> edges = initialGraph.getEdges();
        UnionFind unions = new UnionFind(graph.size());
        for (int i = 0; i < edges.size() && minimalSpanningTree.getEdges().size() < edges.size() - 1; i++) {
            closestEdgeArray = new Endpoint[graph.size()];
            for (Endpoint edge : edges) {
                Integer u = Integer.parseInt(edge.getNodeU());
                Integer v = Integer.parseInt(edge.getNodeV());
                Integer uParent = unions.find(u);
                Integer vParent = unions.find(v);

                if (uParent.equals(vParent))
                    continue;

                Integer weight = graph.get(u.toString()).get(v.toString());

                if (closestEdgeArray[uParent] == null)
                    closestEdgeArray[uParent] = edge;
                if (closestEdgeArray[vParent] == null)
                    closestEdgeArray[vParent] = edge;

                if (weight < graph.get(closestEdgeArray[uParent].getNodeU())
                        .get(closestEdgeArray[uParent].getNodeV()))
                    closestEdgeArray[uParent] = edge;
                if (weight < graph.get(closestEdgeArray[vParent].getNodeU())
                        .get(closestEdgeArray[vParent].getNodeV()))
                    closestEdgeArray[vParent] = edge;
            }
            Arrays.stream(closestEdgeArray).forEach(enpoint -> {
                Endpoint edge = enpoint;
                if (edge != null) {
                    Integer u = Integer.parseInt(edge.getNodeU());
                    Integer v = Integer.parseInt(edge.getNodeV());

                    Integer weight = graph.get(u.toString()).get(v.toString());
                    if (!unions.find(u).equals(unions.find(v))) {
                        if (!minimalSpanningTree.getAdjacencyList().containsKey(u.toString()))
                            minimalSpanningTree.addVertex(u.toString());
                        if (!minimalSpanningTree.getAdjacencyList().containsKey(v.toString()))
                            minimalSpanningTree.addVertex(v.toString());
                        minimalSpanningTree.addEdge(u.toString(), new Pair(v.toString(), weight));
                        treeWeight += weight;
                        unions.union(u, v);
                    }
                }
            });
        }
    }

}
