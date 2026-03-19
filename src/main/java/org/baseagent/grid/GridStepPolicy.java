package org.baseagent.grid;

import org.baseagent.HasStep;
import org.baseagent.sim.Simulation;

public interface GridStepPolicy extends HasStep {
    @Override
    void step(Simulation simulation);
}
