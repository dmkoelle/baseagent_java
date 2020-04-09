package org.baseagent.path;

import org.baseagent.network.Network;

public class PathNetwork extends Network<Intersection, Segment> {

	public PathNetwork() { 
	}
	
	public Segment createSegment(PathComponent... components) {
		Segment path = new Segment(components);
		return path;
	}
	
}
