package org.baseagent.schedule;

import java.util.function.Consumer;

import org.baseagent.sim.Simulation;

public abstract class ScheduledEvent {
	private Consumer<Simulation> thingToDo;
	
	public ScheduledEvent(Consumer<Simulation> thingToDo) {
		this.thingToDo = thingToDo;
	}
	
	public void execute(Simulation simulation) {
		this.thingToDo.accept(simulation);
	}
	
	public abstract boolean isApplicable(Simulation simulation);
	
	public abstract boolean removeWhenDone();
}
