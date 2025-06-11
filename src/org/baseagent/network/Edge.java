package org.baseagent.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** TODO: Bidirectional edges */
public class Edge<T, R> {
	private String id;
	private Node<T> sourceNode;
	private Node<T> destinationNode;
	private Map<String, Object> payload;
	private Predicate<R> doesEdgeApply;
	private Consumer<R> toDoOnEdge;

	public Edge(String id, Node<T> sourceNode, Node<T> destinationNode) {
		this.id = id;
		this.sourceNode = sourceNode;
		this.destinationNode = destinationNode;
		this.doesEdgeApply = (t -> true);
		this.toDoOnEdge = (t -> { });
		this.payload = new HashMap<>();
	}
	
	public Edge(Node<T> sourceNode, Node<T> destinationNode, Predicate<R> doesEdgeApply, Consumer<R> toDoOnEdge) {
		this(UUID.randomUUID().toString(), sourceNode, destinationNode);
		this.doesEdgeApply = doesEdgeApply;
		this.toDoOnEdge = toDoOnEdge;
	}
	
	public String getId() {
		return this.id;
	}
	
	public Node<T> getSourceNode() {
		return this.sourceNode;
	}
	
	public Node<T> getDestinationNode() {
		return this.destinationNode;
	}
	
	public Map<String, Object> getPayload() {
		return this.payload;
	}
	
	public boolean applies(R object) {
		return doesEdgeApply.test(object);
	}
	
	public void doOnEdge(R object) {
		toDoOnEdge.accept(object);
	}
	
	@Override
	public String toString() {
		return "Edge("+id+"' from "+sourceNode+" to "+destinationNode + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof Edge)) return false;
		Edge<T, R> e2 = (Edge<T, R>)o;
		
		return (this.getId().equals(e2.getId()) && 
				this.getSourceNode().equals(e2.getSourceNode()) &&
				this.getDestinationNode().equals(e2.getDestinationNode()));
	}
	
	@Override
	public int hashCode() {
		return this.getId().hashCode() + 37*this.getSourceNode().hashCode() + 41*this.getDestinationNode().hashCode();
	}
}
