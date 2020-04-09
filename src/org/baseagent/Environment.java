package org.baseagent;

import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;

public class Environment extends SimulationComponent implements HasStep {
	public Environment() {
		super();
	}
	
	public SimulationComponent.Type getType() {
		return SimulationComponent.Type.ENVIRONMENT;
	}

	@Override
	public void step(Simulation simulation) { }
}
