package org.baseagent.sim;

public interface SimulationListener {
	// Lifecycle events
	default public void onSimulationStarted(Simulation simulation) { }
	default public void onSimulationEnded(Simulation simulation) { }
	default public void onBeforeStepStarted(Simulation simulation) { }
	default public void onStepStarted(Simulation simulation) { }
	default public void onAfterStepStarted(Simulation simulation) { }
	default public void onBeforeStepEnded(Simulation simulation) { }
	default public void onStepEnded(Simulation simulation) { }
	default public void onAfterStepEnded(Simulation simulation) { }

	// Intentional (user-directed) events
	default public void onSimulationPaused(Simulation simulation) { }
	default public void onSimulationResumed(Simulation simulation) { }
	default public void onSimulationStopped(Simulation simulation) { }
}
