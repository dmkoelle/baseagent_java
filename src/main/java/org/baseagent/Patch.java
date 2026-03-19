package org.baseagent;

import org.baseagent.grid.Grid;
import org.baseagent.sim.SimulationComponent;

/**
 * A Patch is like a rubber stamp applied to each location on a grid.
 */
public abstract class Patch extends SimulationComponent {
    @Override
    public SimulationComponent.Type getType() {
        return SimulationComponent.Type.PATCH;
    }

    // TODO: Patch should be generalized to take a Universe and a Position
    public abstract void applyPatch(Grid grid, int x, int y); 
}
