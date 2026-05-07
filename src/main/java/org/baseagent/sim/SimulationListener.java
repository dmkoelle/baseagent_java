package org.baseagent.sim;

public interface SimulationListener {
    // Lifecycle events
    default void onSimulationStarted(Simulation simulation) { }

    default void onSimulationEnded(Simulation simulation) { }

    default void onBeforeStepStarted(Simulation simulation) { }

    default void onStepStarted(Simulation simulation) { }

    default void onAfterStepStarted(Simulation simulation) { }

    default void onBeforeStepEnded(Simulation simulation) { }

    default void onStepEnded(Simulation simulation) { }

    default void onAfterStepEnded(Simulation simulation) { }

    default void onSimulationComponentAdded(Simulation simulation, SimulationComponent component) { }

    default void onSimulationComponentRemoved(Simulation simulation, SimulationComponent component) { }

    // Intentional (user-directed) events
    default void onSimulationPaused(Simulation simulation) { }

    default void onSimulationResumed(Simulation simulation) { }

    default void onSimulationStopped(Simulation simulation) { }
}
