package org.baseagent;

import org.baseagent.sim.SimulationComponent;

public class Item extends SimulationComponent {
	private String itemName;
	private Inventory contents;
	
	public Item(String name) {
		this.itemName = name;
		this.contents = new Inventory();
	}

	@Override
	public Type getType() {
		return SimulationComponent.Type.ITEM;
	}

}
