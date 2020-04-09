package org.baseagent.schedule;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.baseagent.sim.Simulation;

public class ConditionalEvent extends ScheduledEvent {
	private Predicate<Simulation> condition;
	
	public ConditionalEvent(Predicate<Simulation> condition, Consumer<Simulation> thingToDo) {
		super(thingToDo);
		this.condition = condition;
	}

	@Override
	public boolean isApplicable(Simulation simulation) {
		return (condition.test(simulation));
	}

	@Override
	public boolean removeWhenDone() {
		return false;
	}
}
