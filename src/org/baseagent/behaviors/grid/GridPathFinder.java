package org.baseagent.behaviors.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridCell;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridPosition;

public class GridPathFinder {
    
//    // Interface for grid cell information
//    public interface GridCell {
//        boolean isTraversable();
//        double getTraversalCost();
//    }
    
    // Node class for A* algorithm
    private static class PathNode implements Comparable<PathNode> {
        public final int x, y;
        public double gCost; // Cost from start
        public double hCost; // Heuristic cost to end
        public double fCost; // Total cost (g + h)
        public PathNode parent;
        
        public PathNode(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public int compareTo(PathNode other) {
            return Double.compare(this.fCost, other.fCost);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof PathNode)) return false;
            PathNode other = (PathNode) obj;
            return x == other.x && y == other.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    /**
     * Finds the optimal path from start to end position through traversable cells
     * with minimum traversal cost.
     * 
     * @param grid The grid containing cell information
     * @param startX Starting x coordinate
     * @param startY Starting y coordinate
     * @param endX Ending x coordinate
     * @param endY Ending y coordinate
     * @return List of GridPosition waypoints from start to end, or empty list if no path exists
     */
    public static List<GridPosition> findOptimalPath(Grid grid, GridLayer layer, int startX, int startY, int endX, int endY, Predicate<GridCell> isCellTraversible, ToDoubleFunction<GridCell> traversalCost) {
        // Validate input positions
        startX = grid.getBoundsPolicy().boundX(startX);
        startY = grid.getBoundsPolicy().boundY(startY);
        endX = grid.getBoundsPolicy().boundX(endX);
        endY = grid.getBoundsPolicy().boundY(endY);
        
        if (!isCellTraversible.test(grid.getCell(layer, startX, startY)) || !isCellTraversible.test(grid.getCell(layer, endX, endY))) {
            return new ArrayList<>();
        }
        
        // A* algorithm implementation
        PriorityQueue<PathNode> openSet = new PriorityQueue<>();
        Set<PathNode> closedSet = new HashSet<>();
        Map<String, PathNode> openSetMap = new HashMap<>();
        
        PathNode startNode = new PathNode(startX, startY);
        startNode.gCost = 0;
        startNode.hCost = calculateHeuristic(startX, startY, endX, endY);
        startNode.fCost = startNode.gCost + startNode.hCost;
        
        openSet.add(startNode);
        openSetMap.put(getNodeKey(startX, startY), startNode);
        
        // Direction vectors for 8-directional movement (including diagonals)
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
        
        while (!openSet.isEmpty()) {
            PathNode current = openSet.poll();
            openSetMap.remove(getNodeKey(current.x, current.y));
            closedSet.add(current);
            
            // Check if we reached the destination
            if (current.x == endX && current.y == endY) {
                return reconstructPath(current);
            }
            
            // Explore neighbors
            for (int i = 0; i < 8; i++) {
                int neighborX = current.x + dx[i];
                int neighborY = current.y + dy[i];
                
                if (!grid.isValidPosition(neighborX, neighborY)) {
                    continue;
                }
                
                GridCell neighborCell = grid.getCell(layer, neighborX, neighborY);
                if (!isCellTraversible.test(neighborCell)) {
                    continue;
                }
                
                PathNode neighbor = new PathNode(neighborX, neighborY);
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                
                // Calculate movement cost (diagonal movement costs more)
                double movementCost = (i < 4 && i % 2 == 1) ? 1.0 : Math.sqrt(2); // Diagonal vs orthogonal
                double tentativeGCost = current.gCost + movementCost + traversalCost.applyAsDouble(neighborCell);
                
                String neighborKey = getNodeKey(neighborX, neighborY);
                PathNode existingNeighbor = openSetMap.get(neighborKey);
                
                if (existingNeighbor == null || tentativeGCost < existingNeighbor.gCost) {
                    if (existingNeighbor != null) {
                        openSet.remove(existingNeighbor);
                    }
                    
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = calculateHeuristic(neighborX, neighborY, endX, endY);
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;
                    neighbor.parent = current;
                    
                    openSet.add(neighbor);
                    openSetMap.put(neighborKey, neighbor);
                }
            }
        }
        
        // No path found
        return new ArrayList<>();
    }
    
    /**
     * Alternative method that returns waypoints as coordinate pairs
     */
    public static List<int[]> findOptimalPathCoordinates(Grid grid, int startX, int startY, int endX, int endY) {
        List<GridPosition> path = findOptimalPath(grid, startX, startY, endX, endY);
        List<int[]> coordinates = new ArrayList<>();
        
        for (GridPosition pos : path) {
            coordinates.add(new int[]{(int)pos.getCellX(), (int)pos.getCellY()});
        }
        
        return coordinates;
    }
    
    private static double calculateHeuristic(int x1, int y1, int x2, int y2) {
        // Using Euclidean distance as heuristic
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
    
    private static String getNodeKey(int x, int y) {
        return x + "," + y;
    }
    
    private static List<GridPosition> reconstructPath(PathNode endNode) {
        List<GridPosition> path = new ArrayList<>();
        PathNode current = endNode;
        
        while (current != null) {
            path.add(0, new GridPosition(current.x, current.y));
            current = current.parent;
        }
        
        return path;
    }
}

