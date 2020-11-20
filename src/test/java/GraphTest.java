import BoruvkaAlgorithmStructures.MinimalSpanningTree;
import DinicAlgoritmStructures.DinicAlgorithm;
import GraphStructures.Graph;
import GraphStructures.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static GraphStructures.Graph.yenAlgorithm;

public class GraphTest {

    static Graph graphFromFile;
    static String TEST_DIR = "/Users/islepov/Documents/GitHub/Graphs/src/main/resources/";


    static {
        try {
            graphFromFile = Graph.createGraphFromFile(TEST_DIR + "adjacencyListTest.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createFromFileTest() {
        Graph hardcodedGraph = new Graph();
        Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();
        adjacencyList.put("1", new HashMap<>());
        adjacencyList.get("1").put("2", 1);
        adjacencyList.put("2", new HashMap<>());
        adjacencyList.get("2").put("1", 1);
        hardcodedGraph.setAdjacencyList(adjacencyList);
        hardcodedGraph.setOriented(false);
        Assertions.assertEquals(hardcodedGraph.getAdjacencyList(), graphFromFile.getAdjacencyList());
    }

    @Test
    public void addVertexAndEdgeTest() {
        Graph graph = new Graph(false, new HashMap<>());
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addEdge("1", new Pair("2", 1));
        Assertions.assertEquals(graphFromFile.getAdjacencyList(), graph.getAdjacencyList());
    }

    @Test
    public void cloneTest() {
        Graph clone = Graph.cloneGraph(graphFromFile);
        clone.deleteVertex("1");
        Assertions.assertNotEquals(clone, graphFromFile);
    }

    @Test
    public void deleteVertexTest() {
        Graph clone = Graph.cloneGraph(graphFromFile);
        clone.deleteVertex("2");
        clone.deleteVertex("1");
        Assertions.assertEquals(new HashMap<>(), clone.getAdjacencyList());
    }

    @Test
    public void deleteEdgeTest() {
        Graph clone = Graph.cloneGraph(graphFromFile);
        clone.deleteEdge("1", "2");
        Assertions.assertNotEquals(clone, graphFromFile);
    }

    @Test
    public void saveTest() throws IOException {
        Graph clone = Graph.cloneGraph(graphFromFile);
        clone.saveToFile("/Users/islepov/Documents/Github/Graphs/src/main/resources/");
        Graph newGraph =
                Graph.createGraphFromFile("/Users/islepov/Documents/Github/Graphs/src/main/resources/list.json");
        Assertions.assertEquals(clone.getAdjacencyList(), newGraph.getAdjacencyList());
    }

    @Test
    public void vertexPowerText() {
        Map<String, Integer> power = new HashMap<>();
        power.put("1", 1);
        power.put("2", 1);
        Assertions.assertEquals(power, graphFromFile.vertexPower());
    }

    @Test
    public void checkLoopVertex() {
        List<String> list = new ArrayList<>();
        list.add("2");
        Assertions.assertEquals(list, graphFromFile.incomingOutgoingVertices("1"));
    }

    @Test
    public void deleteEqualsEdgesTest() {
        Graph graph = Graph.cloneGraph(graphFromFile);
        graph.addVertex("3");
        graph.addEdge("3", new Pair("2", 1));
        graph.addEdge("3", new Pair("1", 1));
        graph.addEdge("3", new Pair("3", 1));
        Graph nextGraph = Graph.cloneGraph(graph);
        nextGraph.deleteEdge("1", "2");
        Graph finalGraph = graph.newGraphByDeleteEqualsPowerVertices();
        Assertions.assertEquals(nextGraph.getAdjacencyList(),
                finalGraph.getAdjacencyList());
    }

    @Test
    public void testIsLooped() throws IOException {
        Graph loopGraph = Graph.createGraphFromFile(TEST_DIR + "loopTest.json");
        Graph notLoopGraph = Graph.createGraphFromFile(TEST_DIR + "notLoopTest.json");
        Assertions.assertEquals(true, loopGraph.isLooped());
        Assertions.assertEquals(false, notLoopGraph.isLooped());
    }

    @Test
    public void testMinLoops() throws IOException {
        Graph simpleLoopGraph = Graph.createGraphFromFile(TEST_DIR + "loopTest.json");
        Graph graph = Graph.createGraphFromFile(TEST_DIR + "minLoopsTest.json");
        Graph secondGraph = Graph.createGraphFromFile(TEST_DIR + "secondMinLoopsTest.json");
        Set<String> vertices = new HashSet<>();
        vertices.add("3");
        vertices.add("4");
        Set<String> nextVertices = new HashSet<>();
        nextVertices.add("4");
        Set<String> simple = new HashSet<>();
        simple.add("1");
        simple.add("2");
        simple.add("3");
        System.out.println(graph.minLoops());
        System.out.println(secondGraph.minLoops());
        System.out.println(simpleLoopGraph.minLoops());
        System.out.println(simpleLoopGraph.getEdges());
    }

    @Test
    public void testBoruvkaAlgorithm() throws IOException {
        Graph newGraph = new Graph(false, new HashMap<>());
        newGraph.addVertex("0");
        newGraph.addVertex("1");
        newGraph.addVertex("2");
        newGraph.addVertex("3");
        newGraph.addVertex("4");
        newGraph.addEdge("0", new Pair("1", 8));
        newGraph.addEdge("0", new Pair("2", 5));
        newGraph.addEdge("1", new Pair("2", 3));
        newGraph.addEdge("1", new Pair("3", 11));
        newGraph.addEdge("2", new Pair("3", 15));
        newGraph.addEdge("2", new Pair("4", 100));
        newGraph.addEdge("3", new Pair("4", 7));
        Graph graph = Graph.createGraphFromFile(TEST_DIR + "adjacencyListTest.json");
        MinimalSpanningTree minimalSpanningTree =
                new MinimalSpanningTree(newGraph);
        Graph boruvkaMST = minimalSpanningTree.getMinimalSpanningTree();

        System.out.println(boruvkaMST.getAdjacencyList().size());
        System.out.println(boruvkaMST.getAdjacencyList());
        System.out.println(minimalSpanningTree.getTreeWeight());
    }

    @Test
    public void testFindRadius() throws IOException {
        Graph graph = Graph.createGraphFromFile(TEST_DIR + "findMinusRadiusTest.json");
        System.out.println(graph.findRadius());
    }

    @Test
    public void testBellmanFord() throws IOException {
        Graph graph = Graph.createGraphFromFile(TEST_DIR + "findRadiusTest.json");
        System.out.println(yenAlgorithm(graph,"5", "1", 4));
    }

    @Test
    public void testFloydWarshell() throws IOException{
        Graph graph = Graph.createGraphFromFile(TEST_DIR + "floydWarhellTest.json");
        Graph secondGraph = Graph.createGraphFromFile(TEST_DIR + "floydWarhellSecondTest.json");
        graph.floydWarshell();
        secondGraph.floydWarshell();
    }

    @Test
    public void testDinic() throws IOException{
        DinicAlgorithm algorithm = new DinicAlgorithm(Graph.createGraphFromFile(TEST_DIR + "dinicTest.json"));
        System.out.println(( algorithm.dinic("1","6")));
    }
}
