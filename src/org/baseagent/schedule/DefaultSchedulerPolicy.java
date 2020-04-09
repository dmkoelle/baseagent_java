package org.baseagent.schedule;

import java.util.List;

import org.baseagent.sim.Simulation;

public class DefaultSchedulerPolicy implements SchedulerPolicy {
	@Override
	public List<ScheduledEvent> organizeScheduledEvents(Simulation simulation, List<ScheduledEvent> scheduledEvents) {
		return scheduledEvents;
	}
}
