package org.baseagent.embodied;

import org.baseagent.grid.GridPosition;

// TODO: Is a Processor really just an Embodied Behavior? Should a Behavior be a ConnectedComponent? Or should a Processor implement Behavior?
public class Processor<T, U> extends GridPosition {
    public Processor() {
        super();
    }

    public void process(EmbodiedAgent agent) {
        // default no-op; subclasses should override
    }
}