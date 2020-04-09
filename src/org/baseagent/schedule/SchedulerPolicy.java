package org.baseagent.schedule;

import java.util.List;

import org.baseagent.sim.Simulation;

public interface SchedulerPolicy {
	public List<ScheduledEvent> organizeScheduledEvents(Simulation simulation, List<ScheduledEvent> scheduledEvents);
}
