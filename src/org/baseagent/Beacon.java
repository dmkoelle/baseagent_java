package org.baseagent;

import org.baseagent.grid.HasGridPosition;
import org.baseagent.signals.Signal;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.SimulationComponent;

public class Beacon extends GridAgent {

	public Beacon() {
		super();
	}
	
	public Beacon(String gridLayerName) {
		super(gridLayerName);
	}

	public Beacon(Signal signal) {
		throw new RuntimeException("Beacon(Signal) not implemented");
	}
	

	@Override
	public Type getType() {
		return SimulationComponent.Type.BEACON;
	}
	
	public boolean reaches(HasGridPosition p2) {
		return true;
	}
	
	public boolean reaches(HasGridPosition large, HasGridPosition small) {
		return true;
	}
	
	public double getSignalValueAt(HasGridPosition p2) {
		return 1.0;
	}

	public double getSignalValueAt(HasGridPosition large, HasGridPosition small) {
		return 1.0;
	}
}
