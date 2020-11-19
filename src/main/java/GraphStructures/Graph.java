package GraphStructures;

import BoruvkaAlgorithmStructures.Endpoint;
import DinicAlgoritmStructures.FlowEdge;
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
        Map<String, Object> graph = new HashMap<>();
        graph.put("oriented", oriented);
        graph.put("adjacencyList", adjacencyList);
        objectMapper.writeValue(Paths.get(directory + "list.json").toFile(), graph);
    }

    public Map<String, Integer> vertexPower() {
        if (this.adjacencyList.keySet().isEmpty()) {
            System.out.println("GraphStructures.Graph is empty");
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
            System.out.println("GraphStructures.Graph is empty");
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
            colored.putAll(dfs(vertex, colored));
            if (colored.get(vertex).equals("Gray"))
                return true;
        }
        return false;
    }

    public Map<String, String> dfs(String vertex, Map<String, String> colored) {
        colored.put(vertex, "Gray");
        Set<String> vertices = this.adjacencyList.get(vertex).keySet();
        for (String vert : vertices) {
            if (!colored.containsKey(vert)) {
                if (dfs(vert, colored).get(vert).equals("Gray"))
                    return colored;
            } else if (colored.get(vert).equals("Gray")) {
                return colored;
            }
        }
        colored.put(vertex, "Black");
        return colored;
    }

    public Map<Integer, Set<String>> minLoops() {
        Map<String, String> colored = new HashMap<>();
        Map<Integer, Set<String>> loops = new HashMap<>();
        Set<String> vertices = this.adjacencyList.keySet();
        Integer loopNumber = 1;
        Integer minLoopLength = Integer.MAX_VALUE;
        for (String vertex : vertices) {
            colored.putAll(dfs(vertex, colored));
            if (colored.get(vertex).equals("Gray")) {
                if (!loops.containsValue(colored.keySet())) {
                    loops.put(loopNumber, new HashSet<>());
                    loops.get(loopNumber).addAll(colored.keySet());
                }
                if (minLoopLength > colored.keySet().size())
                    minLoopLength = colored.keySet().size();
                colored.clear();
                loopNumber++;
            }
        }
        final Integer length = minLoopLength;
        return loops.entrySet()
                .stream()
                .filter(x -> x.getValue().size() == length)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<Endpoint> getEdges() {
        List<Endpoint> edges = new ArrayList<>();
        this.adjacencyList.entrySet().forEach(vertex1 -> {
            vertex1.getValue().entrySet().forEach(vertex2 ->
                    edges.add(new Endpoint(vertex1.getKey(), vertex2.getKey(), vertex2.getValue())));
        });
        return edges;
    }

    public boolean isConnected() {
        Map<String, Boolean> used = new HashMap<>();
        Boolean connected = true;
        for (String startVertex : this.adjacencyList.keySet())
            for (String endVertex : this.adjacencyList.keySet())
                if (!startVertex.equals(endVertex))
                    connected = isConnected(startVertex, endVertex, used);
        return connected;
    }

    public boolean isConnected(String startVertex, String endVertex, Map<String, Boolean> used) {
        if (startVertex.equals(endVertex))
            return true;
        used.put(startVertex, true);
        for (String vertex : this.adjacencyList.get(startVertex).keySet())
            if (!used.containsKey(vertex) && isConnected(vertex, endVertex, used))
                return true;
        return false;
    }

    public Integer findRadius() {
        List<Integer> eccentricityValues = new ArrayList<>();
        for (String vertex : this.adjacencyList.keySet()) {
            eccentricityValues.add(dijkstraAlgorithm(vertex));
        }
        return eccentricityValues.stream().min(Integer::compare).get();
    }

    private Integer dijkstraAlgorithm(String startVertex) {
        Map<String, Boolean> used = new HashMap<>();
        Map<String, Integer> distance = new HashMap<>();
        for (String vertex : this.adjacencyList.keySet())
            distance.put(vertex, Integer.MAX_VALUE);
        distance.put(startVertex, 0);
        for (; ; ) {
            String nearestVertex = "";
            for (String vertex : this.adjacencyList.keySet())
                if (!used.containsKey(vertex) && distance.get(vertex) < Integer.MAX_VALUE
                        && (nearestVertex.equals("")
                        || distance.get(nearestVertex) > distance.get(vertex)))
                    nearestVertex = vertex;
            if (nearestVertex.equals(""))
                break;
            used.put(nearestVertex, true);
            for (String vertex : this.adjacencyList.keySet())
                if (!used.containsKey(vertex) && this.adjacencyList.get(nearestVertex).containsKey(vertex))
                    distance.put(vertex, Math.min(distance.get(vertex),
                            distance.get(nearestVertex) + this.adjacencyList.get(nearestVertex).get(vertex)));
        }
        return distance.values()
                .stream()
                .filter(vertex -> !vertex.equals(Integer.MAX_VALUE)).max(Integer::compare).get();
    }

    static public List<Path> yenAlgorithm(Graph graph, String startVertex, String finalVertex, Integer waysAmount) {
        if (waysAmount <= 0)
            return new ArrayList<>();
        List<Path> minimalWays = new ArrayList<>();
        Queue<Path> candidates = new PriorityQueue<>();
        Path shortestWay = bellmanFord(graph, startVertex, finalVertex);
        minimalWays.add(shortestWay);
        for (int i = 1; i < waysAmount; i++) {
            for (int j = 0; j < minimalWays.get(i - 1).getWay().size() - 1; j++) {
                List<Endpoint> removeEdges = new ArrayList<>();
                String spurNode = minimalWays.get(i - 1).getWay().get(j);
                List<String> rootWay = new ArrayList<>();
                for (int k = 0; k < j; k++)
                    rootWay.add(minimalWays.get(i-1).getWay().get(k));
                for (Path way : minimalWays) {
                    List<String> stub = new ArrayList<>();
                    for (int k = 0; k < j; k++)
                        stub.add(way.getWay().get(k));
                    if (rootWay.equals(stub)) {
                        Endpoint edge = new Endpoint(way.getWay().get(j), way.getWay().get(j + 1),
                                graph.getAdjacencyList().get(way.getWay().get(j)).get(way.getWay().get(j + 1)));
                        graph.deleteEdge(way.getWay().get(j), way.getWay().get(j + 1));
                        graph.deleteEdge(way.getWay().get(j + 1), way.getWay().get(j));
                        removeEdges.add(edge);
                    }
                }
                for (int k = 0; k < rootWay.size() - 1; k++) {
                    Endpoint removed = new Endpoint(rootWay.get(k), rootWay.get(k + 1),
                            graph.getAdjacencyList().get(rootWay.get(k)).get(rootWay.get(k + 1)));
                    if (!spurNode.equals(rootWay.get(k))) {
                        graph.deleteEdge(removed.getNodeU(), removed.getNodeV());
                        graph.deleteEdge(removed.getNodeV(), removed.getNodeU());
                        removeEdges.add(removed);
                    }
                }
                Path spurPath = bellmanFord(graph, spurNode, finalVertex);

                if (!spurPath.getWay().isEmpty()) {
                    Path totalWay = new Path();
                    totalWay.setWay(new ArrayList<>());
                    rootWay.forEach(vertex -> totalWay.getWay().add(vertex));
                    totalWay.getWay().addAll(spurPath.getWay());
                    if (!candidates.contains(totalWay))
                        candidates.add(totalWay);
                }
                removeEdges.forEach(edge -> graph.addEdge(edge.getNodeU(), new Pair(edge.getNodeV(), edge.getWeight())));
            }
            boolean isNewPath = true;
            do {
                shortestWay = candidates.poll();
                isNewPath = true;
                if (!shortestWay.getWay().isEmpty()){
                    for (Path way : minimalWays)
                        if (way.equals(shortestWay)){
                            isNewPath = false;
                            break;
                        }
                }
            } while (!isNewPath);
            if (shortestWay.getWay().isEmpty())
                break;
            minimalWays.add(shortestWay);
        }
        return minimalWays;
    }

    static public Path bellmanFord(Graph graph, String startVertex, String finalVertex) {
        Map<String, Double> distance = new HashMap<>();
        Map<String, String> predecessor = new HashMap<>();
        graph.getAdjacencyList().keySet().forEach(vertex -> {
            distance.put(vertex, Double.MAX_VALUE);
            predecessor.put(vertex, null);
        });
        distance.put(startVertex, 0d);
        for (int i = 0; i < graph.getAdjacencyList().keySet().size(); i++) {
            for (Endpoint edge : graph.getEdges()) {
                if (distance.get(edge.getNodeU()) != Double.MAX_VALUE &&
                        distance.get(edge.getNodeV()) > distance.get(edge.getNodeU()) + edge.getWeight()) {
                    distance.put(edge.getNodeV(), distance.get(edge.getNodeU()) + edge.getWeight());
                    predecessor.put(edge.getNodeV(), edge.getNodeU());
                }
            }
        }
        for (Endpoint edge : graph.getEdges()) {
            if (distance.get(edge.getNodeV()) > distance.get(edge.getNodeU()) + edge.getWeight())
                throw new RuntimeException("Negate cycle");
        }

        String currentVertex = predecessor.get(finalVertex);
        List<String> way = new ArrayList<>();
        way.add(finalVertex);
        for (; ; ) {
            way.add(currentVertex);
            currentVertex = predecessor.get(currentVertex);
            if (currentVertex.equals(startVertex)) {
                way.add(startVertex);
                break;
            }
        }
        Collections.reverse(way);
        return new Path(way, distance.get(finalVertex));
    }

    public void floydWarshell() {
        Graph newGraph = cloneGraph(this);
        newGraph.adjacencyList
                .keySet()
                .forEach(firstVertex -> newGraph.adjacencyList.entrySet().forEach(secondVertex -> {
                    if (!secondVertex.getValue().containsKey(firstVertex))
                        secondVertex.getValue().put(firstVertex, Integer.MAX_VALUE);
                    secondVertex.getValue().put(secondVertex.getKey(), 0);
                }));
        for (String firstVertex : this.adjacencyList.keySet())
            for (String secondVertex : this.adjacencyList.keySet())
                for (String thirdVertex : this.adjacencyList.keySet())
                    if (newGraph.adjacencyList.get(secondVertex).get(firstVertex) < Integer.MAX_VALUE
                            && newGraph.adjacencyList.get(firstVertex).get(thirdVertex) < Integer.MAX_VALUE)
                        newGraph.adjacencyList.get(secondVertex).put(thirdVertex,
                                Math.min(newGraph.adjacencyList.get(secondVertex).get(thirdVertex),
                                        newGraph.adjacencyList.get(secondVertex).get(firstVertex)
                                                + newGraph.adjacencyList.get(firstVertex).get(thirdVertex)));
        for (String firstVertex : this.adjacencyList.keySet())
            for (String secondVertex : this.adjacencyList.keySet())
                for (String thirdVertex : this.adjacencyList.keySet())
                    if (newGraph.adjacencyList.get(firstVertex).get(thirdVertex) < Integer.MAX_VALUE
                            && newGraph.adjacencyList.get(thirdVertex).get(thirdVertex) < 0
                            && newGraph.adjacencyList.get(thirdVertex).get(secondVertex) < Integer.MAX_VALUE)
                        newGraph.adjacencyList.get(firstVertex).put(secondVertex, Integer.MIN_VALUE);
        System.out.println(newGraph.getAdjacencyList());
    }

    public Map<String, List<FlowEdge>> createFlowsGraph() {
        Map<String, List<FlowEdge>> flowGraph = new HashMap<>();
        this.adjacencyList.keySet().forEach(vertex -> flowGraph.put(vertex, new ArrayList<>()));
        this.adjacencyList.entrySet().forEach(vertex -> {
            vertex.getValue().entrySet().forEach(edge -> {
                flowGraph.get(vertex.getKey()).add(new FlowEdge(edge.getKey(),
                        flowGraph.get(edge.getKey()).size(),
                        (double) edge.getValue()));
                flowGraph.get(edge.getKey()).add(new FlowEdge(vertex.getKey(),
                        flowGraph.get(vertex.getKey()).size() - 1,
                        0.0));
            });
        });
        return flowGraph;
    }


}