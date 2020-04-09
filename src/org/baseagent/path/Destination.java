package org.baseagent.path;

public class Destination {
	private Segment path;
	private double location;
	private String name;
	
	public Destination(Segment path, double location, String name) {
		this.path = path;
		this.location = location;
		this.name = name;
		
		this.path.addDestination(this);
	}
}
