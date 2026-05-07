package org.baseagent.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.baseagent.grid.GridPosition;
import org.baseagent.util.BaseAgentMath;

/**
 * Utility class to load road network data from a formatted text file and create
 * a Network instance representing the road connections.
 * 
 * Example road network data file: - Lines that start with # are comments -
 * Otherwise, lines are x1,y1, x2,y2 pairs - You can also use # after pairs
 * 
 */
public class GridNetworkLoader {

    // Pattern to match coordinate pairs: "x,y"
    private static final Pattern COORDINATE_PATTERN = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");

    /**
     * Load road network from file content
     * 
     * @param fileContent The content of the road network file
     * @return Network instance representing the road network
     */
    public static Network<GridPosition, Object> loadFromString(String fileContent) {
        Network<GridPosition, Object> pathNetwork = new Network<>();

        String[] lines = fileContent.split("\n");

        for (String line : lines) {
            line = line.trim();

            // Skip empty lines and comment lines
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // Remove comments that appear after coordinates
            int commentIndex = line.indexOf('#');
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex).trim();
            }

            // Find all coordinate pairs in the line
            Matcher matcher = COORDINATE_PATTERN.matcher(line);
            GridPosition previousGridPosition = null;

            while (matcher.find()) {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                GridPosition currentGridPosition = new GridPosition(x, y);

                // Add node if it doesn't exist
                if (pathNetwork.getNode(currentGridPosition) == null) {
                    pathNetwork.addNode(currentGridPosition);
                }

                // If we have a previous GridPosition, create an edge between them
                if (previousGridPosition != null) {
                    Node<GridPosition> sourceNode = pathNetwork.getNode(previousGridPosition);
                    Node<GridPosition> destinationNode = pathNetwork.getNode(currentGridPosition);

                    // Create edge with unique ID based on coordinates
                    String edgeId = previousGridPosition.toString() + "->" + currentGridPosition.toString();
                    Edge<GridPosition, Object> edge = new Edge<>(edgeId, sourceNode, destinationNode);

                    // Calculate and store distance
                    double distance = BaseAgentMath.distance(currentGridPosition, previousGridPosition);
                    edge.getPayload().put(Network.DISTANCE, distance);

                    pathNetwork.addEdge(edge);
                }

                previousGridPosition = currentGridPosition;
            }
        }

        return pathNetwork;
    }

    /**
     * Load road network from file
     * 
     * @param filename Path to the road network file
     * @return Network instance representing the road network
     * @throws IOException if file cannot be read
     */
    public static Network<GridPosition, Object> loadFromFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return loadFromString(content.toString());
    }

    /**
     * Example usage and testing method
     */
    public static void main(String[] args) {
        // Your road network data as a string
        String roadData =
                "###\n" +
                "###\n" +
                "### TRAVERSIBLE NETWORK\n" +
                "### On a 140x160 grid\n" +
                "###\n" +
                "# 'A'\n" +
                "1,41 , 17,66 # Edge of grid to Intersection with 'B'\n" +
                "17,66 , 36,86\n" +
                "36,86  , 56,140 # Edge of grid\n" +
                "# 'B'\n" +
                "17,66 , 27,54 # Intersection with 'A'\n" +
                "44,43 , 52, 43 # Dead end\n";

        // Load the network
        Network<GridPosition, Object> roadNetwork = loadFromString(roadData);

        // Print some statistics
        System.out.println("Road Network loaded successfully!");
        System.out.println("Number of nodes (intersections/endGridPositions): " + roadNetwork.getNodes().size());
        System.out.println("Number of edges (road segments): " + roadNetwork.getEdges().size());

        // Debug: Check for self-loops and show first few edges
        System.out.println("\nFirst 3 edges:");
        int edgeCount = 0;
        for (Edge<GridPosition, Object> edge : roadNetwork.getEdges()) {
            if (edgeCount++ < 3) {
                GridPosition source = edge.getSourceNode().getObject();
                GridPosition dest = edge.getDestinationNode().getObject();
                double distance = (Double) edge.getPayload().get(Network.DISTANCE);
                System.out.printf("  %s -> %s (%.2f)\n", source, dest, distance);

                // Check for self-loops
                if (source.equals(dest)) {
                    System.out.println("    *** WARNING: Self-loop detected! ***");
                }
            }
        }

        // Debug: Check connectivity from start node
        GridPosition startGridPosition = new GridPosition(1, 41);
        Node<GridPosition> startNode = roadNetwork.getNode(startGridPosition);
        if (startNode != null) {
            System.out.println("\nConnections FROM start node " + startGridPosition + ":");
            var edgesFrom = roadNetwork.getEdgesFrom(startNode);
            if (edgesFrom != null && !edgesFrom.isEmpty()) {
                for (Edge<GridPosition, Object> edge : edgesFrom) {
                    double distance = (Double) edge.getPayload().get(Network.DISTANCE);
                    System.out.printf("  -> %s (distance: %.2f)\n", edge.getDestinationNode().getObject(), distance);
                }
            } else {
                System.out.println("  No outgoing edges found!");
            }
        }

        // Example: Find shortest path from first GridPosition to a specific destination
        startGridPosition = new GridPosition(1, 41);
        startNode = roadNetwork.getNode(startGridPosition);

        if (startNode != null) {
            System.out.println("\nCalculating shortest paths from " + startGridPosition + "...");
            var distances = roadNetwork.getShortestPath(startNode);
            System.out.println("Calculation complete!");
            System.out.println("\nShortest distances from " + startGridPosition + ":");

            // Show distances to first few nodes as example
            int count = 0;
            for (var entry : distances.entrySet()) {
                if (count++ < 5 && entry.getValue() != Double.MAX_VALUE) {
                    System.out.printf("  To %s: %.2f units\n", entry.getKey().getObject(), entry.getValue());
                }
            }
        }

        // Example: Show connections from a specific intersection
        GridPosition intersection = new GridPosition(17, 66); // This should be a major intersection
        Node<GridPosition> intersectionNode = roadNetwork.getNode(intersection);
        if (intersectionNode != null) {
            System.out.println("\nConnections from intersection " + intersection + ":");
            var edges = roadNetwork.getEdgesFrom(intersectionNode);
            if (edges != null) {
                for (Edge<GridPosition, Object> edge : edges) {
                    double distance = (Double) edge.getPayload().get(Network.DISTANCE);
                    System.out.printf("  -> %s (distance: %.2f)\n", edge.getDestinationNode().getObject(), distance);
                }
            }
        }
    }
}