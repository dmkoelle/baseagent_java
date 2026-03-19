package org.baseagent.metrics;

import org.baseagent.sim.Simulation;

public interface Metric {
    void evaluate(Simulation sim);
}
