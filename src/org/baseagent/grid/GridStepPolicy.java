package org.baseagent.grid;

import org.baseagent.HasStep;
import org.baseagent.sim.Simulation;

public interface GridStepPolicy extends HasStep {
	@Override
	public void step(Simulation simulation);
}
