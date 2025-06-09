package org.baseagent.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.baseagent.grid.GridPosition;
import org.baseagent.util.BaseAgentMath;

/**
 * Utility class to load road network data from a formatted text file
 * and create a Network instance representing the road connections.
 */
public class RoadNetwork {
    
    // Pattern to match coordinate pairs: "x,y"
    private static final Pattern COORDINATE_PATTERN = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");
    
    /**
     * Load road network from file content
     * @param fileContent The content of the road network file
     * @return Network instance representing the road network
     */
    public static Network<GridPosition, Object> loadFromString(String fileContent) {
        Network<GridPosition, Object> roadNetwork = new Network<>();
        
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
                if (roadNetwork.getNode(currentGridPosition) == null) {
                    roadNetwork.addNode(currentGridPosition);
                }
                
                // If we have a previous GridPosition, create an edge between them
                if (previousGridPosition != null) {
                    Node<GridPosition> sourceNode = roadNetwork.getNode(previousGridPosition);
                    Node<GridPosition> destinationNode = roadNetwork.getNode(currentGridPosition);
                    
                    // Create edge with unique ID based on coordinates
                    String edgeId = previousGridPosition.toString() + "->" + currentGridPosition.toString();
                    Edge<GridPosition, Object> edge = new Edge<>(edgeId, sourceNode, destinationNode);
                    
                    // Calculate and store distance
                    double distance = BaseAgentMath.distance(currentGridPosition, previousGridPosition);
                    edge.getPayload().put(Network.DISTANCE, distance);
                    
                    roadNetwork.addEdge(edge);
                }
                
                previousGridPosition = currentGridPosition;
            }
        }
        
        return roadNetwork;
    }
    
    /**
     * Load road network from file
     * @param filename Path to the road network file
     * @return Network instance representing the road network
     * @throws IOException if file cannot be read
     */
    public static Network<GridPosition, Object> loadFromFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
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
        String roadData = """
###
###
### ROAD NETWORK FOR PUMPKIN NECK MAP
### (Hand-entered)
###
###
# "A": Left road that moves SSE
1,41 , 17,66 # End of image to Intersection with "B"
17,66 , 36,86
36,86  , 56,140 # End of image
# "B": Road from leftmost road that goes into field
17,66 , 27,54 # Intersection with "A"
27,54 , 34,43 # Intersection with "D"
34,43 , 44,43 # Intersection with "C"
44,43 , 52, 43 # Dead end
# "C": Long road from that field road that goes to other field through the forest
44,43 , 53,52 # Intersection with "D"
53,52 , 56,55 # Intersection with "E"
56,55 , 62,67
62,67 , 108,78
108,78 , 118,74
118,74 , 147,83 # Intersection with "F" and "G"
# "D": Arc that connects in leftmost field
27,54 , 37,57 # Intersection with "B"
37,57 , 53,52 # Intersectoin with "C"
# "E": Spur road that goes into field second-from-left
56,55 , 72,54 # Intersection with "C"
72,54 , 72,51 # Dead end
# "F": Spur road at top of rightmost field
147,83 , 159,74 # Intersection with "C" and "G"
157,74 , 169,71 # Dead end
# "G": Road connecting long road "C" with long road "H"
147,83 , 161,116 # Intersection with "C" and "G" to Intersection with "H"
# "H": Long road that cuts through the bottom of the rightmost field
148, 140 , 152,129 # End of image to Intersection with "I"
152,129 , 161,116 # Intersection with "G"
161,116 , 173,110 # Intersection with loop "J"
173,110 , 176,109 # Intersection with loop "K"
176,109 , 182,106 # Intersection with loop "K" again
182,106 , 186,103 # Intersection with spur "L"
186,103 , 203,93 # Intersection with road "M"
203,93 , 226,80 # Intersection with road "N"
# "I": Stuff at the bottom of the image. Dead-end road first.
152,129 , 145,127 # Intersection with "I2"
145,127 , 135,122 # Intersection with "I3"
135,122 , 129,119
129,119 , 119,128 # Dead end
# "I2" is a little spur road connecting "I" to "I3"
145,127 , 131,126 # Intersection with "I" to Intersection with "I3"
# "I3" is a road that ends in a loop, we'll just call it a dead end
135,122 , 118,133 # Intersection with "I" to Dead End
# "J" is a big loop in the big field
173,110 , 161,103
161,103 , 163,92
163,92 , 171,88
171,88 , 180,95
180,95 , 180,102
180,102 , 173,110 
# "K" is a small loop
176,109 , 183,113
183,113 , 182,106
# "L" is a straight spur
186,103 , 194,119
# "M" is a long road going up tp the water. Ignore the loop at the top.
203,93 , 197,27 # Intersection with "N"
197,27 , 188,15 # End at water
# "N" is the long road on the right side of the map
197,27 , 205,27
205,27 , 214,33
214,33 , 226,80 # Intersection with "M"
226,80 , 247,111 # End of image
        """;
        
        // Load the network
        Network<GridPosition, Object> roadNetwork = loadFromString(roadData);
        
        // Print some statistics
        System.out.println("Road Network loaded successfully!");
        System.out.println("Number of nodes (intersections/endGridPositions): " + roadNetwork.getNodes().size());
        System.out.println("Number of edges (road segments): " + roadNetwork.getEdges().size());
        
        // Debug: Check for self-loops and show first few edges
        System.out.println("\nFirst 10 edges:");
        int edgeCount = 0;
        for (Edge<GridPosition, Object> edge : roadNetwork.getEdges()) {
            if (edgeCount++ < 10) {
                GridPosition source = edge.getSoureNode().getObject();
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
                    System.out.printf("  -> %s (distance: %.2f)\n", 
                        edge.getDestinationNode().getObject(), distance);
                }
            } else {
                System.out.println("  No outgoing edges found!");
            }
        }
        
        // Example: Find shortest path from first GridPosition to a specific destination
        startGridPosition = new GridPosition(1, 41);
        startNode = roadNetwork.getNode(startGridPosition);
        
        if (startNode != null) {
            System.out.println("\\nCalculating shortest paths from " + startGridPosition + "...");
            var distances = roadNetwork.getShortestPath(startNode);
            System.out.println("Calculation complete!");
            System.out.println("\\nShortest distances from " + startGridPosition + ":");
            
            // Show distances to first few nodes as example
            int count = 0;
            for (var entry : distances.entrySet()) {
                if (count++ < 5 && entry.getValue() != Double.MAX_VALUE) {
                    System.out.printf("  To %s: %.2f units\\n", 
                        entry.getKey().getObject(), entry.getValue());
                }
            }
        }
        
        // Example: Show connections from a specific intersection
        GridPosition intersection = new GridPosition(17, 66); // This should be a major intersection
        Node<GridPosition> intersectionNode = roadNetwork.getNode(intersection);
        if (intersectionNode != null) {
            System.out.println("\\nConnections from intersection " + intersection + ":");
            var edges = roadNetwork.getEdgesFrom(intersectionNode);
            if (edges != null) {
                for (Edge<GridPosition, Object> edge : edges) {
                    double distance = (Double) edge.getPayload().get(Network.DISTANCE);
                    System.out.printf("  -> %s (distance: %.2f)\\n", 
                        edge.getDestinationNode().getObject(), distance);
                }
            }
        }
    }
}