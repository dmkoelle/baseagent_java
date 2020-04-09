package org.baseagent;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
	private Map<Object, Integer> thingsAndCounts;
	
	public Inventory() {
		thingsAndCounts = new HashMap<>();
	}
	
	public boolean has(Object thing) {
		return (thingsAndCounts.get(thing) != null) && (thingsAndCounts.get(thing) > 0);
	}
	
	public void forget(Object thing) {
		thingsAndCounts.remove(thing);
	}
	
	public void clear(Object thing) {
		thingsAndCounts.put(thing, 0);
	}
	
	public void remove(Object thing) {
		thingsAndCounts.put(thing, count(thing)-1);
	}
	
	public void add(Object thing) {
		thingsAndCounts.put(thing, count(thing)+1);
	}

	public void take(Object thing) {
		remove(thing);
	}
	
	public void place(Object thing) {
		add(thing);
	}
	
	public int count(Object thing) {
		if (!has(thing)) {
			return 0;
		} else {
			return thingsAndCounts.get(thing);
		}
	}
}
