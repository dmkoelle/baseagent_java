package org.baseagent.path;

public interface HasPathPosition {
	public Segment getCurrentSegment();
	public double getHeading();
	public void setNextSegment(Segment segment);
	public Segment getNextSegment();
	public Segment deterimeNextSegment(Intersection intersection);
}
