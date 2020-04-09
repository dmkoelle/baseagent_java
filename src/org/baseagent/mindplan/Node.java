package org.baseagent.mindplan;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.embodied.ConnectedComponent;
import org.baseagent.sim.Simulation;

// A possible implementation of the "General Architecture of Pluggable Things"

public class Node extends ConnectedComponent<Node> {
	private List<Satisfiable> satisfiables;
	
	public Node() {
		this.satisfiables = new ArrayList<>();
	}
	
	public List<Satisfiable> getSatisfiables() {
		return this.satisfiables;
	}

	public double getUrge() {
		return 1.0;
	}
	
	public boolean isSatisfied(Simulation simulation) {
		for (Satisfiable s : getSatisfiables()) {
			if (s.isSatisfied(simulation)) return false;
		}
		return true;
	}

}
