package org.baseagent.grid;

import org.baseagent.sim.Simulation;

public class NoPatchGridStepPolicy implements GridStepPolicy {
	private Grid grid;
	
	public NoPatchGridStepPolicy(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void step(Simulation simulation) { }
}
