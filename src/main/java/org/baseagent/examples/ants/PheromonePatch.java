package org.baseagent.examples.ants;

import org.baseagent.Patch;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;

/**
 * Patch that diffuses and dissipates pheromone values on the "pheromone" layer.
 */
public class PheromonePatch extends Patch {
    private String layerName = "pheromone";
    // tuned defaults to avoid explosive spread
    private double spread = 0.12; // neighbor contribution factor (smaller)
    private double dissipation = 0.03; // fraction lost each step (larger)
    private double maxPheromone = 6.0; // clamp to modest maximum

    public PheromonePatch() {
    }

    public PheromonePatch(double spread, double dissipation) {
        this.spread = spread;
        this.dissipation = dissipation;
    }

    @Override
    public void applyPatch(Grid grid, int x, int y) {
        GridLayer<Double> pher = grid.getGridLayer(layerName);
        if (pher == null)
            return;

        Double center = (Double) pher.current().get(x, y);
        if (center == null)
            center = 0.0;

        // average of the 9-neighborhood (uses current read layer)
        double avg = pher.current().average9Neighbors(x, y);

        // diffusion term moves concentration toward neighborhood average
        double diffusion = (avg - center) * spread;

        double next = center * (1.0 - dissipation) + diffusion;
        // clamp to avoid runaway
        if (next < 0.0)
            next = 0.0;
        if (next > maxPheromone)
            next = maxPheromone;

        // write into the write-layer
        pher.set(x, y, next);
    }
}