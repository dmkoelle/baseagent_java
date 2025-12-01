// filepath: p:/Projects/BaseAgent/baseagent_java/src/org/baseagent/util/AStar.java
package org.baseagent.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;

import org.baseagent.map.MapLayer;

/**
 * Simple A* implementation that operates on a MapLayer grid overlay.
 * Cells marked as water (~) are considered passable; others are blocked.
 *
 * Returns paths as lists of integer [col,row] pairs (column, row).
 */
public class AStar {

    private static class Node {
        int x, y;
        double g; // cost from start
        double h; // heuristic to goal
        Node parent;

        Node(int x, int y, double g, double h, Node parent) {
            this.x = x; this.y = y; this.g = g; this.h = h; this.parent = parent;
        }

        double f() { return g + h; }
    }

    /**
     * Find path between two lat/lon points on the given grid map layer.
     * Converts lat/lon to grid cell indices using MapLayer.latLonToCell.
     * Returns list of {col,row} pairs from start (inclusive) to goal (inclusive), or empty list if none found.
     */
    public static List<int[]> findPath(MapLayer layer, double startLat, double startLon, double endLat, double endLon) {
        int[] s = layer.latLonToCell(startLat, startLon);
        int[] e = layer.latLonToCell(endLat, endLon);
        return findPath(layer, s[0], s[1], e[0], e[1]);
    }

    /**
     * Find path between two grid cells (col,row).
     */
    public static List<int[]> findPath(MapLayer layer, int startCol, int startRow, int endCol, int endRow) {
        int cols = layer.getGridCols();
        int rows = layer.getGridRows();
        if (cols <= 0 || rows <= 0) return new ArrayList<>();

        if (!inBounds(startCol, startRow, cols, rows) || !inBounds(endCol, endRow, cols, rows)) {
            return new ArrayList<>();
        }

        // Quick check: start or end on non-passable cell -> no path
        if (!isPassable(layer, startCol, startRow) || !isPassable(layer, endCol, endRow)) return new ArrayList<>();

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        Map<String, Double> gScore = new HashMap<>();
        Map<String, Node> allNodes = new HashMap<>();

        Node start = new Node(startCol, startRow, 0.0, heuristic(startCol, startRow, endCol, endRow), null);
        open.add(start);
        gScore.put(key(startCol,startRow), 0.0);
        allNodes.put(key(startCol,startRow), start);

        int[][] neighbors = new int[][] { {1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1} };

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.x == endCol && current.y == endRow) {
                return reconstructPath(current);
            }

            for (int[] nOff : neighbors) {
                int nx = current.x + nOff[0];
                int ny = current.y + nOff[1];
                if (!inBounds(nx, ny, cols, rows)) continue;
                if (!isPassable(layer, nx, ny)) continue;

                double moveCost = (nOff[0] == 0 || nOff[1] == 0) ? 1.0 : Math.sqrt(2.0);
                double tentativeG = current.g + moveCost;
                String nk = key(nx, ny);
                Double knownG = gScore.get(nk);
                if (knownG == null || tentativeG < knownG) {
                    double h = heuristic(nx, ny, endCol, endRow);
                    Node neighborNode = new Node(nx, ny, tentativeG, h, current);
                    gScore.put(nk, tentativeG);
                    allNodes.put(nk, neighborNode);
                    open.add(neighborNode);
                }
            }
        }

        return new ArrayList<>(); // no path
    }

    private static List<int[]> reconstructPath(Node n) {
        List<int[]> path = new ArrayList<>();
        Node cur = n;
        while (cur != null) {
            path.add(0, new int[] { cur.x, cur.y });
            cur = cur.parent;
        }
        return path;
    }

    private static boolean inBounds(int x, int y, int cols, int rows) {
        return (x >= 0 && x < cols && y >= 0 && y < rows);
    }

    private static String key(int x, int y) { return x + "," + y; }

    private static double heuristic(int x1, int y1, int x2, int y2) {
        // Euclidean distance in cell-space
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.hypot(dx, dy);
    }

    private static boolean isPassable(MapLayer layer, int col, int row) {
        Object o = layer.get(col, row);
        if (o == null) return false; // treat unknown as blocked
        String s = o.toString();
        // Passable if we have water marker ~ (user suggested '~' for water)
        if (s.length() > 0 && s.charAt(0) == '~') return true;
        // Also treat numeric or other explicit water tokens (case-insensitive) as passable
        if (s.equalsIgnoreCase("water") || s.equalsIgnoreCase("sea") || s.equalsIgnoreCase("~")) return true;
        return false;
    }
}
