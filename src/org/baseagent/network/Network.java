package org.baseagent.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.baseagent.sim.Universe;

public class Network<T, R> implements Universe {
	private Map<T, Node<T>> nodes;
	private Map<String, Edge<T,R>> edges;
	private Map<Node<T>, List<Edge<T,R>>> edgesFromNode;
	private Map<Node<T>, List<Edge<T,R>>> edgesToNode;
	private List<NetworkListener> networkListeners;
	
	public Network() {
		nodes = new HashMap<>();
		edges = new HashMap<>();
		edgesFromNode = new HashMap<>();
		edgesToNode = new HashMap<>();
		this.networkListeners = new ArrayList<>();
	}
	
	public void addNode(T object) {
		this.addNode(new Node<T>(object));
	}
	
	public void addNode(Node<T> node) {
		nodes.put(node.getObject(), node);
		fireNodeAdded(node);
	}

	public Node getNode(T object) {
		return nodes.get(object);
	}
	
	public boolean hasNode(String id) {
		return nodes.containsKey(id);
	}
	
	public boolean hasNode(Node<T> node) {
		return nodes.containsValue(node);
	}
	
	public Collection<Node<T>> getNodes() {
		return nodes.values();
	}

	public void removeNode(T object) {
		this.removeNode(nodes.get(object));
	}
	
	public void removeNode(Node<T> node) {
		nodes.remove(node.getObject());
		edgesToNode.remove(node);
		edgesFromNode.remove(node);
		fireNodeRemoved(node);
	}
	
	public void addEdge(String edgeName, String sourceNodeId, String destinationNodeId) {
		Node<T> sourceNode = nodes.get(sourceNodeId);
		Node<T> destinationNode = nodes.get(destinationNodeId);
		
		Edge<T,R> edge = new Edge(edgeName, sourceNode, destinationNode);
		edges.put(edgeName, edge);
		addEdgeToEdgesList(edge, edgesToNode, sourceNode);
		addEdgeToEdgesList(edge, edgesFromNode, destinationNode);
		
		fireEdgeAdded(edge);
	}
	
	public void addEdge(Edge<T,R> edge) {
		edges.put(edge.getId(), edge);
		addEdgeToEdgesList(edge, edgesToNode, edge.getSoureNode());
		addEdgeToEdgesList(edge, edgesFromNode, edge.getDestinationNode());
		fireEdgeAdded(edge);
	}
	
	private void addEdgeToEdgesList(Edge<T,R> edge, Map<Node<T>, List<Edge<T,R>>> map, Node<T> node) {
		List<Edge<T,R>> edges = null;
		if (map.containsKey(node)) {
			edges = map.get(node);
		} else {
			edges = new ArrayList<Edge<T,R>>();
		}
		edges.add(edge);
	}
	
	public Collection<Edge<T,R>> getEdgesTo(T object) {
		return this.getEdgesTo(nodes.get(object));
	}

	public Collection<Edge<T,R>> getEdgesTo(Node node) {
		return edgesToNode.get(node);
	}
	
	public Collection<Edge<T,R>> getEdgesFrom(T object) {
		return this.getEdgesFrom(nodes.get(object));
	}

	public Collection<Edge<T,R>> getEdgesFrom(Node<T> node) {
		return edgesFromNode.get(node);
	}
	
	public Collection<Edge<T,R>> getEdgesBetween(Node<T> sourceNode, Node<T> destinationNode) {
		Collection<Edge<T,R>> edgesFromSource = getEdgesFrom(sourceNode);
		Collection<Edge<T,R>> edgesFromDestination = getEdgesTo(destinationNode);
		Collection<Edge<T,R>> retVal = new ArrayList<>();
		for (Edge<T,R> edge : edgesFromSource) {
			if (edgesFromDestination.contains(edge)) {
				retVal.add(edge);
			}
		}
		return retVal;
	}

	public void removeEdge(String id) {
		this.removeEdge(edges.get(id));
	}
	
	public void removeEdge(Edge<T,R> edge) {
		edges.remove(edge.getId());
		edgesToNode.values().stream().forEach(edgeList -> { if (edgeList.contains(edge)) edgeList.remove(edge); });
		edgesFromNode.values().stream().forEach(edgeList -> { if (edgeList.contains(edge)) edgeList.remove(edge); });
		fireEdgeRemoved(edge);
	}

	public boolean hasEdge(String id) {
		return edges.containsKey(id);
	}
	
	public boolean hasEdge(Edge<T,R> edge) {
		return edges.containsValue(edge);
	}

	public void addNetworkListener(NetworkListener listener) {
		networkListeners.add(listener);
	}

	public void removeNetworkListener(NetworkListener listener) {
		networkListeners.remove(listener);
	}

	public List<NetworkListener> getNetworkListeners() {
		return networkListeners;
	}

	private void fireNodeAdded(Node<T> node) {
		for (NetworkListener listener : networkListeners) {
			listener.onNodeAdded(node);
		}
	}
	
	private void fireNodeRemoved(Node<T> node) {
		for (NetworkListener listener : networkListeners) {
			listener.onNodeRemoved(node);
		}
	}
	
	private void fireEdgeAdded(Edge<T,R> edge) {
		for (NetworkListener listener : networkListeners) {
			listener.onEdgeAdded(edge);
		}
	}
	
	private void fireEdgeRemoved(Edge<T,R> edge) {
		for (NetworkListener listener : networkListeners) {
			listener.onEdgeRemoved(edge);
		}
	}
	
	@Override
	public String toString() {
		return "Network"; // TODO Make Network.toString more fully functional
	}
}
