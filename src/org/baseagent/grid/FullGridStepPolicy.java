package org.baseagent.grid;

import org.baseagent.Patch;
import org.baseagent.sim.Simulation;

public class FullGridStepPolicy implements GridStepPolicy {
	private Grid grid;
	
	public FullGridStepPolicy(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void step(Simulation simulation) {
		for (Patch patch : simulation.getPatches()) {
			for (int y=0; y < grid.getHeightInCells(); y++) {
				for (int x=0; x < grid.getWidthInCells(); x++) {
					patch.applyPatch(grid, x, y);
				}
			}
		}		
	}
}
