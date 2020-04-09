package org.baseagent.schedule;

import java.util.function.Consumer;

import org.baseagent.sim.Simulation;

public class RecurringEvent extends ScheduledEvent {
	private long offsetFromZero;
	private long interval;
	
	public RecurringEvent(long interval, Consumer<Simulation> thingToDo) {
		this(0L, interval, thingToDo);
	}

	public RecurringEvent(long offsetFromZero, long interval, Consumer<Simulation> thingToDo) {
		super(thingToDo);
		this.offsetFromZero = offsetFromZero;
		this.interval = interval;
	}

	@Override
	public boolean isApplicable(Simulation simulation) {
		return ((simulation.getStepTime() - offsetFromZero) % interval == 0L);
	}

	@Override
	public boolean removeWhenDone() {
		return false;
	}
}
