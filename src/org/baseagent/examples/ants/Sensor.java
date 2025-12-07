package org.baseagent.examples.ants;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridAgent;
import org.baseagent.grid.GridPosition;

/**
 * Simple sensor that samples neighboring pheromone intensities and returns
 * a preferred travel direction (radians) or NaN if no signal found.
 */
public class Sensor {
    /**
     * Sense surrounding cells and compute a weighted direction toward higher pheromone.
     * Returns NaN when there is no measurable pheromone.
     */
    public double senseDirection(Grid grid, GridAgent agent, String pheromoneLayer, String wallLayer) {
        int cx = agent.getCellX();
        int cy = agent.getCellY();

        double sumX = 0.0;
        double sumY = 0.0;
        double total = 0.0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if ((dx == 0) && (dy == 0)) continue;
                int nx = cx + dx;
                int ny = cy + dy;
                if (!grid.isValidPosition(nx, ny)) continue;
                Object wall = grid.getGridLayer(wallLayer).current().get(nx, ny);
                if (wall != null) continue; // blocked
                Object o = grid.getGridLayer(pheromoneLayer).current().get(nx, ny);
                double v = 0.0;
                if (o instanceof Double) v = (Double)o;
                if (v <= 0.0) continue;
                // weight by distance (diagonals sqrt(2) => normalize)
                double w = v / Math.sqrt(dx*dx + dy*dy);
                sumX += w * dx;
                sumY += w * dy;
                total += w;
            }
        }

        if (total <= 0.0) return Double.NaN;
        return Math.atan2(sumY, sumX);
    }
}
