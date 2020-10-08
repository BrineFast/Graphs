import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Graph {

    private boolean oriented;
    private Map<String, Map<String, Integer>> adjacencyList;

    public static Graph createGraphFromFile(String adjacencyListDirectory) throws IOException {
        File file = new File(adjacencyListDirectory);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder input = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            input.append(line);
        }
        reader.close();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(input.toString(), new TypeReference<Graph>() {
        });
    }

    public static Graph cloneGraph(Graph graph) {
        Map<String, Map<String, Integer>> newList = new HashMap<>();
        graph.getAdjacencyList().entrySet().forEach(vert -> {
            Map<String, Integer> newSubList = new HashMap<>();
            String key = vert.getKey();
            vert.getValue().entrySet().forEach(subVert -> {
                String subKey = subVert.getKey();
                Integer subValue = subVert.getValue();
                newSubList.put(subKey, subValue);
            });
            newList.put(key, newSubList);
        });
        boolean isOriented = graph.oriented;
        return new Graph(isOriented, newList);
    }

    public void addVertex(String vertex) {
        if (!this.adjacencyList.containsKey(vertex))
            this.adjacencyList.put(vertex, new HashMap<>());
        else System.out.println("This vertex already exists");
    }

    public void addEdge(String vertex, Pair edge) {
        if (!this.adjacencyList.containsKey(edge.getVertex())
                && !this.adjacencyList.containsKey(vertex)) {
            System.out.println(("No such vertex has been found"));
            return;
        } else if (this.adjacencyList.get(vertex).containsKey(edge.getVertex())) {
            System.out.println("Vertex " + edge.getVertex() + " already have edge with " + vertex);
            return;
        }
        this.getAdjacencyList().get(vertex).put(edge.getVertex(), edge.getWeight());
        if (!this.oriented)
            this.getAdjacencyList().get(edge.getVertex()).put(vertex, edge.getWeight());
    }

    public void deleteEdge(String fromVertex, String toVertex) {
        if (this.adjacencyList.containsKey(fromVertex)
                && this.adjacencyList.containsKey(toVertex)) {
            this.getAdjacencyList().get(fromVertex).remove(toVertex);
            if (!this.oriented)
                this.getAdjacencyList().get(toVertex).remove(fromVertex);
        } else
            System.out.println(("No such vertex has been found"));
    }

    public void deleteVertex(String vertex) {
        if (this.adjacencyList.containsKey(vertex)) {
            this.adjacencyList.remove(vertex);
            this.adjacencyList.entrySet().forEach(vert -> {
                vert.getValue().remove(vertex);
            });
        } else
            System.out.println(("No such vertex has been found"));
    }

    public void saveToFile(String directory) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(Paths.get(directory + "list.json").toFile(), this);
    }

    public Map<String, Integer> vertexPower() {
        if (this.adjacencyList.keySet().isEmpty()) {
            System.out.println("Graph is empty");
            return new HashMap<>();
        }
        if (this.oriented) {
            Map<String, Integer> vertPow = new HashMap<>();
            this.adjacencyList.entrySet().forEach(vertex1 ->
                    this.adjacencyList.entrySet().forEach(vertex2 -> {
                        String key = vertex1.getKey();
                        if (vertex2.getValue().containsKey(key) && vertPow.containsKey(key))
                            vertPow.put(key, vertPow.get(key) + 1);
                        else if (vertex2.getValue().containsKey(key))
                            vertPow.put(key, 0);
                    }));
            return vertPow;
        }
        return this.adjacencyList
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, vertex -> vertex.getValue().size()));
    }

    public List<String> incomingOutgoingVertices(String vertex) {
        if (this.adjacencyList.containsKey(vertex))
            return this.adjacencyList
                    .entrySet()
                    .stream()
                    .filter(vert -> vert.getValue().containsKey(vertex)
                            && this.adjacencyList.get(vertex).containsKey(vert.getKey()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        System.out.println("No such vertex has been found");
        return new ArrayList<>();
    }

    public Graph newGraphByDeleteEqualsPowerVertices() {
        if (this.adjacencyList.keySet().isEmpty()) {
            System.out.println("Graph is empty");
            return new Graph();
        }
        Graph graph = cloneGraph(this);
        Map<String, Integer> powers = graph.vertexPower();
        powers.entrySet().forEach(vertex ->
                powers.entrySet().forEach(vertex2 -> {
                    if (vertex.getValue().equals(vertex2.getValue())
                            && !vertex.getKey().equals(vertex2.getKey()))
                        graph.deleteEdge(vertex.getKey(), vertex2.getKey());
                }));
        return graph;
    }

    public boolean isLooped() {
        Map<String, String> colored = new HashMap<>();
        Set<String> vertices = this.adjacencyList.keySet();
        for (String vertex : vertices) {
            colored.putAll(isLooped(vertex, colored));
            if (colored.get(vertex).equals("Gray"))
                return true;
        }
        return false;
    }

    public Map<String, String> isLooped(String vertex, Map<String, String> colored) {
        colored.put(vertex, "Gray");
        Set<String> edges = this.adjacencyList.get(vertex).keySet();
        for (String vert : edges) {
            if (!colored.containsKey(vert)) {
                if (isLooped(vert, colored).get(vert).equals("Gray"))
                    return colored;
            }
            else if (colored.get(vert).equals("Gray")){
                return colored;
            }
        }
        colored.put(vertex, "Black");
        return colored;
    }
}