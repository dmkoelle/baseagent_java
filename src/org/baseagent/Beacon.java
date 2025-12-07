package org.baseagent;

import org.baseagent.grid.GridAgent;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.signals.Signal;
import org.baseagent.sim.SimulationComponent;
import org.baseagent.HasStep;

public class Beacon extends GridAgent implements HasStep {

	private Signal signal;

	public Beacon() {
		super();
	}
	
	public Beacon(String gridLayerName) {
		super(gridLayerName);
	}

	public Beacon(Signal signal) {
		super();
		this.signal = signal;
	}

	public Beacon(String gridLayerName, Signal signal) {
		super(gridLayerName);
		this.signal = signal;
	}
	

	@Override
	public Type getType() {
		return SimulationComponent.Type.BEACON;
	}
	
	/** Return the signal this beacon emits (may be null) */
	public Signal getSignal() {
		return this.signal;
	}

	/** Simulation step - default no-op; beacons may override if they need to change state */
	@Override
	public void step(org.baseagent.sim.Simulation s) {
		// default: do nothing
	}
	
	public boolean reaches(HasGridPosition p2) {
		// default: single-cell beacon (only reaches its own cell)
		return (getCellX() == p2.getCellX()) && (getCellY() == p2.getCellY());
	}
	
	public boolean reaches(HasGridPosition large, HasGridPosition small) {
		return reaches(small);
	}
	
	/** Default signal intensity at a given position. Subclasses/instances should override. */
	public double getSignalValueAt(HasGridPosition p2) {
		return reaches(p2) ? 1.0 : 0.0;
	}

	public double getSignalValueAt(HasGridPosition large, HasGridPosition small) {
		return getSignalValueAt(small);
	}
	
	/** Maximum integer radius (in cells) this beacon can affect. Default 0 = single cell. Subclasses may override. */
    public int getSignalRadius() {
        return 0;
    }
}