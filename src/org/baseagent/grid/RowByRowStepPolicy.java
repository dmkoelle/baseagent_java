package org.baseagent.grid;

import org.baseagent.Patch;

public class RowByRowStepPolicy implements GridStepPolicy {
	private Grid grid;
	private int y;
	
	public RowByRowStepPolicy(Grid grid) {
		this.grid = grid;
		this.y = 1;
	}
	
	@Override
	public void step() {
		System.out.println(y);
		if (this.y == grid.getHeightInCells()-1) {
			step_scrollingUp();
		} else {
			step_fillingIn();
		}

		for (Patch patch : grid.getSimulation().getPatches()) {
			for (int x=0; x < grid.getWidthInCells(); x++) {
				patch.applyPatch(grid, x, this.y);
			}
		}
		
		if (this.y < grid.getHeightInCells()-1) this.y++;
	}

	private void step_scrollingUp() {
		for (GridLayer layer : grid.getGridLayers()) {
			for (int yy=0; yy < this.y; yy++) {
				for (int x=0; x < grid.getWidthInCells(); x++) {
					layer.next().set(x, yy, layer.current().get(x, yy+1));
				}
			}
		}
		this.y--;
	}

	private void step_fillingIn() {
		for (GridLayer layer : grid.getGridLayers()) {
			for (int yy=0; yy < grid.getHeightInCells(); yy++) {
				for (int x=0; x < grid.getWidthInCells(); x++) {
					layer.next().set(x, yy, layer.current().get(x, yy));
				}
			}
		}
	}
}
