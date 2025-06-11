package org.baseagent.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

import org.baseagent.grid.GridPosition;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.util.BaseAgentMath;

public class NetworkMath<T, R> {
    /**
     * Finds the shortest path between two nodes using Dijkstra's algorithm
     * @param startNode The starting node
     * @param endNode The target node
     * @return List of Node<T> representing the shortest path, or empty list if no path exists
     */
    public static<T, R> List<Node<T>> findShortestPath(Network<T, R> network, Node<T> startNode, Node<T> endNode) {
        if (startNode == null || endNode == null) {
            return new ArrayList<>();
        }
        
        if (startNode.equals(endNode)) {
            return Arrays.asList(startNode);
        }
        
        // Distance map and previous node tracker
        Map<Node<T>, Double> distances = new HashMap<>();
        Map<Node<T>, Node<T>> previous = new HashMap<>();
        Set<Node<T>> visited = new HashSet<>();
        
        // Priority queue for Dijkstra's algorithm
        PriorityQueue<NodeDistance<T>> pq = new PriorityQueue<>(
            Comparator.comparingDouble(nd -> nd.distance)
        );
        
        // Initialize distances
        for (Node<T> node : network.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(startNode, 0.0);
        pq.offer(new NodeDistance<>(startNode, 0.0));
        
        while (!pq.isEmpty()) {
            NodeDistance<T> current = pq.poll();
            Node<T> currentNode = current.node;
            
            if (visited.contains(currentNode)) {
                continue;
            }
            
            visited.add(currentNode);
            
            // If we reached the target, break early
            if (currentNode.equals(endNode)) {
                break;
            }
            
            // Check all neighbors
            for (Node<T> neighbor : getNeighbors(network, currentNode)) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                
                double edgeWeight = getEdgeWeight(currentNode, neighbor);
                double newDistance = distances.get(currentNode) + edgeWeight;
                
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentNode);
                    pq.offer(new NodeDistance<>(neighbor, newDistance));
                }
            }
        }
        
        // Reconstruct path
        return reconstructPath(previous, startNode, endNode);
    }
    
    /**
     * Gets all neighboring nodes for a given node
     */
    private static<T, R> List<Node<T>> getNeighbors(Network<T, R> network, Node<T> node) {
        List<Node<T>> neighbors = new ArrayList<>();
        
        for (Edge<T, R> edge : network.getEdges()) {
            Node<T> fromNode = edge.getSourceNode();
            Node<T> toNode = edge.getDestinationNode();
            
            if (fromNode != null && fromNode.equals(node) && toNode != null) {
                neighbors.add(toNode);
            } else if (toNode != null && toNode.equals(node) && fromNode != null) {
                neighbors.add(fromNode); // Assuming undirected graph
            }
        }
        
        return neighbors;
    }
    
    /**
     * Gets the weight of an edge between two nodes
     * TODO: This method should be specific for GridNetworkMath
     */
    private static<T> double getEdgeWeight(Node<T> fromNode, Node<T> toNode) {
        Optional<HasGridPosition> posop1 = GridNetworkMath.getPossibleGridPositionForNode(fromNode);
        Optional<HasGridPosition> posop2 = GridNetworkMath.getPossibleGridPositionForNode(toNode);
        if (posop1.isPresent() && posop2.isPresent()) {
            double distance = BaseAgentMath.distance(posop1.get(), posop2.get());
            // TODO: Insert more complex ways to incorporate Edge properties in this calculation
            return distance;
        }
        return 1.0;
    }
    
    /**
     * Reconstructs the shortest path from the previous node mapping
     */
    private static<T> List<Node<T>> reconstructPath(Map<Node<T>, Node<T>> previous, Node<T> start, Node<T> end) {
        List<Node<T>> path = new ArrayList<>();
        Node<T> current = end;
        
        // Build path backwards
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }
        
        // Check if we actually found a path to start
        if (path.isEmpty() || !path.get(path.size() - 1).equals(start)) {
            return new ArrayList<>(); // No path found
        }
        
        // Reverse to get path from start to end
        Collections.reverse(path);
        return path;
    }
    

    
    /**
     * Helper class for priority queue in Dijkstra's algorithm
     */
    private static class NodeDistance<T> {
        final Node<T> node;
        final double distance;
        
        NodeDistance(Node<T> node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }
    
    public static<T> double calculateDistanceAlongPath(List<Node<T>> nodes) {
        double distance = 0.0;
        for (int i = 0; i < nodes.size() - 1; i++) {
            distance += getEdgeWeight(nodes.get(i), nodes.get(i+1));
        }
        return distance;
    }
}
