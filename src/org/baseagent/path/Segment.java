package org.baseagent.path;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.network.Edge;
import org.baseagent.network.Network;

public class Segment extends Edge<Segment, PathComponent> {
	private List<Destination> destinations;
	private Directionality directionality;
	private List<PathComponent> pathComponents;
	
	public Segment(PathComponent... pathComponents) {
		this(Directionality.BIDIRECTIONAL, pathComponents);
	}

	public Segment(Directionality directionality, PathComponent... pathComponents) {
		super();
		this.destinations = new ArrayList<>();
		this.directionality = Directionality.BIDIRECTIONAL;
		this.pathComponents = new ArrayList<>();
		
		for (PathComponent pc : pathComponents) {
			this.pathComponents.add(pc);
		}
	}

	public static Segment createSegment(Network<Segment, PathComponent> network, PathComponent... components) {
		return createSegment(network, Directionality.BIDIRECTIONAL, components);
	}
	
	public static Segment createSegment(Network<Segment, PathComponent> network, Directionality directionality, PathComponent... components) {
		Segment segment = new Segment(directionality, components);
		network.addEdge(segment);
		return segment;
	}
	
	public void addDestination(Destination destination) {
		this.destinations.add(destination);
	}
	
	public class Joint extends PathComponent {
		public Joint(int x, int y) {
			super(x, y);
		}
	}
	
	public class EndPoint extends PathComponent {
		public EndPoint(int x, int y) {
			super(x, y);
		}
	}
	
	public enum Directionality { BIDIRECTIONAL, ONE_WAY };
}

