package Question3;



import java.util.*;

    class Edge {
        int source, destination, weight;

        public Edge(int source, int destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }

    class Graph {
        private int vertices;
        private List<Edge> edges;

        public Graph(int vertices) {
            this.vertices = vertices;
            this.edges = new ArrayList<>();
        }

        public void addEdge(int source, int destination, int weight) {
            edges.add(new Edge(source, destination, weight));
        }

        public List<Edge> kruskalMST() {
            List<Edge> result = new ArrayList<>();

            // Sort the edges based on their weights
            edges.sort(Comparator.comparingInt(e -> e.weight));

            // Create a disjoint set
            int[] parent = new int[vertices];
            for (int i = 0; i < vertices; i++) {
                parent[i] = i;
            }

            // Perform Kruskal's algorithm
            for (Edge edge : edges) {
                int rootSource = find(parent, edge.source);
                int rootDest = find(parent, edge.destination);

                // Add the edge to the result only if it doesn't create a cycle
                if (rootSource != rootDest) {
                    result.add(edge);
                    union(parent, rootSource, rootDest);
                }
            }

            return result;
        }

        private int find(int[] parent, int vertex) {
            if (parent[vertex] != vertex) {
                parent[vertex] = find(parent, parent[vertex]); // Path compression
            }
            return parent[vertex];
        }

        private void union(int[] parent, int x, int y) {
            int rootX = find(parent, x);
            int rootY = find(parent, y);
            parent[rootX] = rootY;
        }
    }

    public class Kruskals_b {
        public static void main(String[] args) {
            int vertices = 4;
            Graph graph = new Graph(vertices);

            graph.addEdge(0, 1, 10);
            graph.addEdge(0, 2, 6);
            graph.addEdge(0, 3, 5);
            graph.addEdge(1, 3, 15);
            graph.addEdge(2, 3, 4);

            List<Edge> minimumSpanningTree = graph.kruskalMST();

            System.out.println("Minimum Spanning Tree edges:");
            for (Edge edge : minimumSpanningTree) {
                System.out.println(edge.source + " - " + edge.destination + " : " + edge.weight);
            }
        }
    }


