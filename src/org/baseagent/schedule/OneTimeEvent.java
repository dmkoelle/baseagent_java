package org.baseagent.schedule;

import java.util.function.Consumer;

import org.baseagent.sim.Simulation;

public class OneTimeEvent extends ScheduledEvent {
	private long scheduledTime;
	
	public OneTimeEvent(long time, Consumer<Simulation> thingToDo) {
		super(thingToDo);
		this.scheduledTime = time;
	}

	@Override
	public boolean isApplicable(Simulation simulation) {
		return (simulation.getStepTime() == scheduledTime);
	}

	@Override
	public boolean removeWhenDone() {
		return true;
	}
	
}
