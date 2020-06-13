package org.baseagent.sim;

import org.baseagent.path.HasPathPosition;
import org.baseagent.path.Intersection;
import org.baseagent.path.Segment;
import org.baseagent.ui.Drawable;
import org.baseagent.ui.DrawableAgent;

public class PathAgent extends DrawableAgent implements HasPathPosition {

	private Segment currentSegment;
	private Segment nextSegment;
	private double distanceOnSegment;
	private Drawable drawable;
	
	public PathAgent() {
		super();
	}

	//
	// HasPathPosition
	//

	@Override
	public Segment getCurrentSegment() {
		return this.currentSegment;
	}

	@Override
	public double getHeading() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Segment deterimeNextSegment(Intersection intersection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNextSegment(Segment segment) {
		this.nextSegment = segment;
	}

	@Override
	public Segment getNextSegment() {
		return this.nextSegment;
	}

	
	// HasPathPosition.determineNextPath() is intended to be implemented by subclasses
	
}
