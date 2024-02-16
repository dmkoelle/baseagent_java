package org.baseagent.grid;

import java.util.List;

import org.baseagent.Patch;
import org.baseagent.sim.Simulation;

public class RowByRowStepPolicy implements GridStepPolicy {
	private Grid grid;
	private int y;
	
	public RowByRowStepPolicy(Grid grid) {
		this.grid = grid;
		this.y = 1;
	}
	
	// TODO - Bug where once we scroll, scroll and fill are called every other sim step,
	// but just doing a fill right after a scroll in a single step doesn't work. It's as
	// if something else is changing this.y.
	@Override
	public void step(Simulation simulation) {
		if (this.y == grid.getHeightInCells()-1) {
			System.out.println(simulation.getStepTime()+" Scroll up, y is "+y);
			step_scrollUp();
//			doPatches(simulation.getPatches());
			this.y = grid.getHeightInCells()-2;
		} else {
//		}
//			System.out.println(simulation.getStepTime()+" Fill in, y is "+y+" (if scrolling, should be "+(grid.getHeightInCells()-2)+")");
			step_fillIn();
			doPatches(simulation.getPatches());
			this.y++;
		}
	}

	private void doPatches(List<Patch> patches) {
		for (Patch patch : patches) {
			for (int x=0; x < grid.getWidthInCells(); x++) {
				patch.applyPatch(grid, x, this.y);
			}
		}
	}
	
	private void step_scrollUp() {
		for (GridLayer layer : grid.getGridLayers()) {
			for (int yy=0; yy < grid.getHeightInCells()-1; yy++) {
				for (int x=0; x < grid.getWidthInCells(); x++) {
					layer.next().set(x, yy, layer.current().get(x, yy+1));
				}
			}
		}
	}

	private void step_fillIn() {
		for (GridLayer layer : grid.getGridLayers()) {
			for (int yy=0; yy < grid.getHeightInCells(); yy++) {
				for (int x=0; x < grid.getWidthInCells(); x++) {
					layer.next().set(x, yy, layer.current().get(x, yy));
				}
			}
		}
	}
}
