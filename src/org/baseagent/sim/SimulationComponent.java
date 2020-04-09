package org.baseagent.sim;

import java.util.UUID;

// A SimulationComponent is not a SimulationListener by default because there could be million of them and that would
// really slow down the processing - and in general, components (say agents) don't need to know about simulation
// lifecycle events.
public abstract class SimulationComponent {
	private UUID uuid;
	private Simulation simulation;

	// TODO - should SimulationComponent have a no-arg constructor?
	public SimulationComponent() { 
		this.uuid = UUID.randomUUID();
	}

	public UUID getUUID() {
		return this.uuid;
	}
	
	public SimulationComponent(Simulation simulation) { 
		setSimulation(simulation);
	}
	
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}
	
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	public abstract SimulationComponent.Type getType();
	
	public enum Type { AGENT, GRID, ENVIRONMENT, PATCH, ITEM, COMMUNICATOR, BEACON };
}
