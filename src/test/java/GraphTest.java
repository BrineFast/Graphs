import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphTest {

    static Graph graphFromFile;
    static String TEST_DIR = "/Users/islepov/Downloads/Graphs/src/main/resources/";


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
        adjacencyList.put("1",new HashMap<>());
        adjacencyList.get("1").put("2",1);
        adjacencyList.put("2",new HashMap<>());
        adjacencyList.get("2").put("1",1);
        hardcodedGraph.setAdjacencyList(adjacencyList);
        hardcodedGraph.setOriented(false);
        Assertions.assertEquals(hardcodedGraph.getAdjacencyList(), graphFromFile.getAdjacencyList());
    }

    @Test
    public void addVertexAndEdgeTest(){
        Graph graph = new Graph(false, new HashMap<>());
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addEdge("1",new Pair("2",1));
        Assertions.assertEquals(graphFromFile.getAdjacencyList(),graph.getAdjacencyList());
    }

    @Test
    public void cloneTest(){
        Graph clone = Graph.cloneGraph(graphFromFile);
        clone.deleteVertex("1");
        Assertions.assertNotEquals(clone, graphFromFile);
    }

    @Test
    public void deleteVertexTest(){
        Graph clone = Graph.cloneGraph(graphFromFile);
        clone.deleteVertex("2");
        clone.deleteVertex("1");
        Assertions.assertEquals(new HashMap<>(),clone.getAdjacencyList());
    }

    @Test
    public void deleteEdgeTest(){
        Graph clone = Graph.cloneGraph(graphFromFile);
        clone.deleteEdge("1","2");
        Assertions.assertNotEquals(clone,graphFromFile);
    }

    @Test
    public void saveTest() throws IOException {
        Graph clone = Graph.cloneGraph(graphFromFile);
        clone.saveToFile("/Users/islepov/Downloads/Graphs/src/main/resources/");
        Graph newGraph =
                Graph.createGraphFromFile("/Users/islepov/Downloads/Graphs/src/main/resources/list.json");
        Assertions.assertEquals(clone.getAdjacencyList(),newGraph.getAdjacencyList());
    }

    @Test
    public void vertexPowerText(){
        Map<String, Integer> power = new HashMap<>();
        power.put("1", 1);
        power.put("2", 1);
        Assertions.assertEquals(power, graphFromFile.vertexPower());
    }

    @Test
    public void checkLoopVertex(){
        List<String> list = new ArrayList<>();
        list.add("2");
        Assertions.assertEquals(list, graphFromFile.incomingOutgoingVertices("1"));
    }

    @Test
    public void deleteEqualsEdgesTest(){
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
}
