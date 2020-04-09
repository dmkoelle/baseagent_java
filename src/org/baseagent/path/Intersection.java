package org.baseagent.path;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.baseagent.sim.PathAgent;

public class Intersection extends PathComponent {
	private List<Consumer<PathAgent>> interactionFunctions;
	
	public Intersection(int x, int y) {
		super(x, y);
		this.interactionFunctions = new ArrayList<>();
	}
	
	public List<PathNetwork> getPaths() {
		return null;
	}
	
	public void addIntersectionAction(Consumer<PathAgent> f) {
		this.interactionFunctions.add(f);
	}

	public void removeIntersectionAction(Consumer<PathAgent> f) {
		this.interactionFunctions.remove(f);
	}
}
