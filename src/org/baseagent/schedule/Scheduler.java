package org.baseagent.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationListener;

public class Scheduler implements SimulationListener {
	private SchedulerPolicy policy;
	private List<ScheduledEvent> scheduledEvents;
	
	public Scheduler() {
		this.policy = new DefaultSchedulerPolicy();
		this.scheduledEvents = new ArrayList<>();
	}
	
	public void runScheduledEvents(Simulation simulation) {
		List<ScheduledEvent> applicableEvents = scheduledEvents.stream().filter(event -> event.isApplicable(simulation)).collect(Collectors.toList());
		List<ScheduledEvent> applicableEventsInRunOrder = this.policy.organizeScheduledEvents(simulation, applicableEvents);
		applicableEventsInRunOrder.forEach(event -> event.execute(simulation));
		for (ScheduledEvent event : applicableEvents) {
			if (event.removeWhenDone()) {
				scheduledEvents.remove(event);
			}
		}
	}
	
	public SchedulerPolicy getSchedulerPolicy() {
		return this.policy;
	}
	
	public void setSchedulerPolicy(SchedulerPolicy policy) {
		this.policy = policy;
	}
	
	public void addScheduledEvent(ScheduledEvent event) {
		scheduledEvents.add(event);
	}
	
	public void removeScheduledEvent(ScheduledEvent event) {
		scheduledEvents.remove(event);
	}
	
	public List<ScheduledEvent> getScheduledEvents() {
		return this.scheduledEvents;
	}

	//
	// SimulationListener
	//
	
	@Override
	public void onAfterStepStarted(Simulation simulation) {
		runScheduledEvents(simulation);
	}

}
