package org.baseagent.grid;

import org.baseagent.Patch;

public class FullGridStepPolicy implements GridStepPolicy {
	private Grid grid;
	
	public FullGridStepPolicy(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void step() {
		for (Patch patch : grid.getSimulation().getPatches()) {
			for (int y=0; y < grid.getHeightInCells(); y++) {
				for (int x=0; x < grid.getWidthInCells(); x++) {
					patch.applyPatch(grid, x, y);
				}
			}
		}		
	}
}
