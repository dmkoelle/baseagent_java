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
		// Convert both vectors to Cartesian coordinates
		double x1 = this.magnitude * Math.cos(this.direction);
		double y1 = this.magnitude * Math.sin(this.direction);
		double x2 = v2.magnitude * Math.cos(v2.direction);
		double y2 = v2.magnitude * Math.sin(v2.direction);
		// Sum components
		double xr = x1 + x2;
		double yr = y1 + y2;
		// Convert back to polar
		this.magnitude = Math.sqrt(xr * xr + yr * yr);
		this.direction = Math.atan2(yr, xr);
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