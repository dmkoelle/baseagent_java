package org.baseagent.signals;

import org.baseagent.grid.HasGridPosition;
import org.baseagent.sim.Simulation;

public class PheromonePropagation implements SignalPropagation, HasGridPosition {

	private Signal signal;
	private int cellX;
	private int cellY;
	private double initialValue;
	private double spreadValue;
	private double dissipationRate;
	
	public PheromonePropagation(Signal signal, int cellX, int cellY, double initialValue, double spreadRate, double dissipationRate) {
		this.signal = signal;
		this.cellX = cellX;
		this.cellY = cellY;
		this.initialValue = initialValue;
		this.spreadValue = spreadRate;
		this.dissipationRate = dissipationRate;
	}

	@Override
	public int getCellX() {
		return this.cellX;
	}

	@Override
	public int getCellY() {
		return this.cellY;
	}

	@Override
	public void propagateSignal(Simulation simulation) {
	}
	

}
