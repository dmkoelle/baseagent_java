package org.baseagent.grid;

public class NoPatchGridStepPolicy implements GridStepPolicy {
	private Grid grid;
	
	public NoPatchGridStepPolicy(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void step() { }
}
