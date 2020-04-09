package org.baseagent.path;

import java.util.List;

import org.baseagent.sim.SimulationComponent;

public class Source<T extends SimulationComponent> extends PathComponent {
	private List<T> list;
	
	public Source(int x, int y) {
		super(x, y);
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}
	
	public List<T> getList() {
		return this.list;
	}

	public T take() {
		T item = getList().get(0);
		getList().remove(0);
		return item;
	}
}
