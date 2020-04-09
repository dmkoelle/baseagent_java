package org.baseagent.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.baseagent.Agent;
import org.baseagent.Beacon;
import org.baseagent.HasStep;
import org.baseagent.Patch;
import org.baseagent.comms.Communicator;
import org.baseagent.comms.MessageListener;
import org.baseagent.data.DataCollector;
import org.baseagent.metrics.Metric;
import org.baseagent.schedule.Scheduler;

public class Simulation {
	private Universe universe;
	private Communicator communicator;
	private List<Scheduler> schedulers;
	private List<DataCollector> dataCollectors;
	
	private List<MessageListener> messageListeners;
	private List<HasStep> hasSteps;
	private List<Agent> agents;
	private List<Beacon> beacons;
	private List<Patch> patches; 
	private List<Metric> metrics;

	private Predicate<Simulation> endCondition;
	private Map<String, Object> properties;
	
	private long time;
	private long delayInMillis = 0;
	private boolean paused;
	private boolean stopped;
	private List<SimulationListener> listeners;
	
	public Simulation() {
		this.schedulers = new ArrayList<>();
		this.dataCollectors = new ArrayList<>();

		this.messageListeners = new ArrayList<>();
		this.hasSteps = new ArrayList<>();
		this.agents = new ArrayList<>();
		this.beacons = new ArrayList<>();
		this.patches = new ArrayList<>();
		this.metrics = new ArrayList<>();

		this.properties = new HashMap<>();
		this.listeners = new ArrayList<>();
		this.time = 0L;
	}
	
	public void setUniverse(Universe universe) {
		this.universe = universe;
		((SimulationComponent)universe).setSimulation(this);
		this.listeners.add(universe);
	}
		
	public Universe getUniverse() {
		return this.universe;
	}
	
	public void removeUniverse(Universe universe) {
		this.universe = null;
		this.listeners.remove(universe);
	}
		
	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

	public Communicator getCommunicator() {
		return this.communicator;
	}
	
	public List<Scheduler> getSchedulers() {
		return this.schedulers;
	}
	
	public void addScheduler(Scheduler scheduler) {
		this.schedulers.add(scheduler);
	}
	
	public void removeSchedule(Scheduler scheduler) {
		schedulers.remove(scheduler);
		this.listeners.remove(scheduler);
	}
	
	public List<DataCollector> getDataCollectors() {
		return dataCollectors;
	}
	
//	public void addData(SimulationComponent component, String key, Object value) {
//		this.dataCollector.add(component, key, value);
//	}
//	
//	public void addData(SimulationComponent component, Map<String, Object> data) {
//		this.dataCollector.add(component, data);
//	}
	
	public Map<String, Object> getProperties() {
		return this.properties;
	}
	
	public void setProperties(Map<String, Object> props) {
		this.properties = props;
	}
	
	public void addSimulationComponent(SimulationComponent simulatee) {
		simulatee.setSimulation(this);
		if (simulatee.getType() == SimulationComponent.Type.AGENT) {
			agents.add((Agent)simulatee);
		}
		else if (simulatee.getType() == SimulationComponent.Type.PATCH) {
			patches.add((Patch)simulatee);
		}
		else if (simulatee.getType() == SimulationComponent.Type.BEACON) {
			beacons.add((Beacon)simulatee);
		}
		
		if (simulatee instanceof HasStep) {
			hasSteps.add((HasStep)simulatee);
		}
		
		if (simulatee instanceof MessageListener) {
			messageListeners.add((MessageListener)simulatee);
		}
	}
	
	public void removeSimulationComponent(SimulationComponent simulatee) {
		simulatee.setSimulation(null);
		if (simulatee.getType() == SimulationComponent.Type.AGENT) {
			agents.remove(simulatee);
		}
		else if (simulatee.getType() == SimulationComponent.Type.PATCH) {
			patches.remove((Patch)simulatee);
		}
		
		if (simulatee instanceof HasStep) {
			hasSteps.remove((HasStep)simulatee);
		}

		if (simulatee instanceof MessageListener) {
			messageListeners.remove((MessageListener)simulatee);
		}
	}
	
	public List<Agent> getAgents() {
		return agents;
	}
	
	public List<Beacon> getBeacons() {
		return beacons;
	}
	
	public List<Patch> getPatches() {
		return patches;
	}
	
	public List<HasStep> getHasSteps() {
		return hasSteps;
	}
	
	public List<MessageListener> getMessageListeners() {
		return messageListeners;
	}
	
	public List<Metric> getMetrics() {
		return this.metrics;
	}
	
	public void addSimulationListener(SimulationListener listener) {
		listeners.add(listener);
	}
	
	public void removeSimulationListener(SimulationListener listener) {
		listeners.remove(listener);
	}
	
	private void fireSimulationStarted() {
		for (SimulationListener listener : listeners) {
			listener.onSimulationStarted(this);
		}
	}

	private void fireSimulationEnded() {
		for (SimulationListener listener : listeners) {
			listener.onSimulationEnded(this);
		}
	}

	private void fireBeforeStepStarted() {
		for (SimulationListener listener : listeners) {
			listener.onBeforeStepStarted(this);
		}
	}

	private void fireStepStarted() {
		for (SimulationListener listener : listeners) {
			listener.onStepStarted(this);
		}
	}

	private void fireAfterStepStarted() {
		for (SimulationListener listener : listeners) {
			listener.onAfterStepStarted(this);
		}
	}

	private void fireBeforeStepEnded() {
		for (SimulationListener listener : listeners) {
			listener.onBeforeStepEnded(this);
		}
	}

	private void fireStepEnded() {
		for (SimulationListener listener : listeners) {
			listener.onStepEnded(this);
		}
	}

	private void fireAfterStepEnded() {
		for (SimulationListener listener : listeners) {
			listener.onAfterStepEnded(this);
		}
	}

	private void fireSimulationPaused() {
		for (SimulationListener listener : listeners) {
			listener.onSimulationPaused(this);
		}
	}

	private void fireSimulationResumed() {
		for (SimulationListener listener : listeners) {
			listener.onSimulationResumed(this);
		}
	}

	private void fireSimulationStopped() {
		for (SimulationListener listener : listeners) {
			listener.onSimulationStopped(this);
		}
	}

	public void endWhen(Predicate<Simulation> endCondition) {
		this.endCondition = endCondition;
	}
	
	public void beforeEachStep(Consumer<Simulation> function) {
		listeners.add(new SimulationListener() {
			@Override
			public void onBeforeStepStarted(Simulation simulation) {
				function.accept(simulation);
			}
		});
	}

	public void afterEachStep(Consumer<Simulation> function) {
		listeners.add(new SimulationListener() {
			@Override
			public void onAfterStepEnded(Simulation simulation) {
				function.accept(simulation);
			}
		});
	}

	public long getStepTime() {
		return this.time;
	}
	
	public void reset() {
		this.time = 0L;
	}
	
	public void setDelayAfterEachStep(int millis) {
		this.delayInMillis = millis;
	}

	public long getDelayAfterEachStep() {
		return this.delayInMillis;
	}

	protected void evaluateMetrics() {
		for (Metric metric : metrics) {
			metric.evaluate(this);
		}
	}
	
	public void start() {
		Thread t = new Thread(() -> Simulation.this.start0());
		t.start();
	}
	
	public void pause() {
		this.paused = true;
		fireSimulationPaused();
	}
	
	public void resume() {
		this.paused = false;
		fireSimulationResumed();
	}
	
	public void stop() {
		this.stopped = true;
		fireSimulationStopped();
	}
	
	private void start0() {
		fireSimulationStarted();
		while (!endCondition.test(this) && !stopped) {
			while (paused) {
				sleep(10);
			}

			// OLD WAY:
			// - fireStepStarted
			// - if beforeEachStepConsumer != null, accept(this)
			// - run scheduled events
			// - grid.step
			// - run each hasStep
			// - grid.swap
			// - if afterEachStepConsumer != null, accept(this)
			// - evaluateMetrics
			// - sleep(delay)
			// - time++
			// - fireStepEnded
			
			
			fireBeforeStepStarted();
			fireStepStarted();
			fireAfterStepStarted();

			for (HasStep hasStep : hasSteps) {
				hasStep.step(this);
			}

			fireBeforeStepEnded();
			fireStepEnded();
			fireAfterStepEnded();

//			evaluateMetrics();
//
			sleep(delayInMillis);
			time++;
//
//			fireStepEnded();
		}
		fireSimulationEnded();
	}

    private void sleep(long millis) {
		try {
			Thread.sleep(delayInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
