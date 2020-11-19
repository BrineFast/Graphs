import GraphStructures.Graph;
import GraphStructures.Pair;

import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        boolean start = true;
        Scanner scanner = new Scanner(System.in);
        Graph graph = new Graph(false, new HashMap<>());
        while (start) {
            System.out.println("\n1. Add vertex");
            System.out.println("2. Add edge");
            System.out.println("3. Delete vertex");
            System.out.println("4. Delete edge");
            System.out.println("5. Print adjacency list");
            System.out.println("6. Check vertices powers");
            System.out.println("7. Check loop vertex");
            System.out.println("8. New graph with delete edges of vertices with the equals powers");
            System.out.println("0. Exit");
            System.out.print("\nEnter value: ");
            int num = scanner.nextInt();
            switch (num) {
                case 1: {
                    System.out.print("\nInput vertex: ");
                    graph.addVertex(scanner.next());
                    break;
                }
                case 2: {
                    System.out.print("\nInput start vertex: ");
                    String fromVertex = scanner.next();
                    System.out.print("\nInput end vertex: ");
                    String toVertex = scanner.next();
                    System.out.print("\nInput weight: ");
                    Integer weight = scanner.nextInt();
                    graph.addEdge(fromVertex, new Pair(toVertex, weight));
                    break;
                }
                case 3: {
                    System.out.println("\nInput vertex: ");
                    graph.deleteVertex(scanner.next());
                    break;
                }
                case 4: {
                    System.out.print("\nInput start vertex: ");
                    String fromVertex = scanner.next();
                    System.out.print("\nInput end vertex: ");
                    String toVertex = scanner.next();
                    graph.deleteEdge(fromVertex, toVertex);
                    break;
                }
                case 5: {
                    System.out.println(graph.getAdjacencyList());
                    break;
                }
                case 6: {
                    System.out.println("Vertices power: ");
                    System.out.println(graph.vertexPower());
                    break;
                }
                case 7: {
                    System.out.println("Input initial vertex: ");
                    String vertex = scanner.next();
                    System.out.println(graph.getAdjacencyList());
                    System.out.println(graph.incomingOutgoingVertices(vertex));
                    break;
                }
                case 8: {
                    System.out.println("Initial graph: ");
                    System.out.println(graph.getAdjacencyList());
                    System.out.println("Edited graph: ");
                    System.out.println(graph.newGraphByDeleteEqualsPowerVertices().getAdjacencyList());
                    break;
                }
                case 0: {
                    start = false;
                }
            }
        }
        System.out.println("Stopped");
    }

}
