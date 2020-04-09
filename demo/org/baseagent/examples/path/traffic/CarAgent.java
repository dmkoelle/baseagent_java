package org.baseagent.examples.path.traffic;

import org.baseagent.path.Intersection;
import org.baseagent.path.Segment;
import org.baseagent.sim.PathAgent;
import org.baseagent.sim.SimulationComponent;

public class CarAgent extends PathAgent {
	public CarAgent() {
		super();
	}

	@Override
	public double getHeading() {
		// TODO: Seems like PathAgent.getHeading() should be auto-calculated in the Path classes!
		return 0;
	}

	@Override
	public Segment deterimeNextSegment(Intersection intersection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getType() {
		return SimulationComponent.Type.AGENT;
	}
}
