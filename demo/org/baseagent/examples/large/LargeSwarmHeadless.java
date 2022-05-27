package org.baseagent.examples.large;

import org.baseagent.grid.Grid;
import org.baseagent.grid.NoPatchGridStepPolicy;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;

public class LargeSwarmHeadless {
	public static void main(String[] args) {
		LargeSwarmHeadless app = new LargeSwarmHeadless();
        Simulation simulation = app.setupSimulation();
        simulation.start();
	}
	
	protected Simulation setupSimulation() {
		Grid grid = new Grid(800, 600);
		grid.setStepPolicy(new NoPatchGridStepPolicy(grid));
		
		Simulation simulation = new Simulation();
		simulation.setUniverse(grid);
		
		for (int i=0; i < 10000; i++) {
			GridAgent flocker = new Flocker(i);
			simulation.add(flocker);
			flocker.placeRandomly();
		}

		simulation.endWhen(sim -> sim.getStepTime() == 5000);
		simulation.setDelayAfterEachStep(50);

		return simulation;
	}
}
