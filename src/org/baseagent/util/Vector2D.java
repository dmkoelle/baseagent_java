package org.baseagent.util;

public class Vector2D {
	private double magnitude;
	private double direction;
	
	public Vector2D(double magnitude, double direction) {
		this.magnitude = magnitude;
		this.direction = direction;
	}
	
	public double getMagnitude() {
		return this.magnitude;
	}
	
	public double getDirection() {
		return this.direction;
	}
	
	public void add(Vector2D v2) {
		this.magnitude = Math.sqrt(this.magnitude * this.magnitude + v2.magnitude * v2.magnitude);
		this.direction = Math.atan2(this.direction, v2.direction);
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o == null) || (!(o instanceof Vector2D))) return false;
		Vector2D v2 = (Vector2D)o;
		return ((v2.magnitude == this.magnitude) && (v2.direction == this.direction));
	}
	
	@Override
	public int hashCode() {
		return (int)(this.magnitude * 37 + this.direction * 43);
	}
	
	@Override
	public String toString() {
		String objectId = super.toString();
		return objectId+"(magnitude="+magnitude+", direction="+direction+")";
	}
}
