package org.baseagent.statemachine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.network.Edge;
import org.baseagent.network.Network;
import org.baseagent.network.Node;

public class StateMachine implements Behavior {
	private Network<State, Agent> network;
	private Map<String, Node<State>> nodesByStateName;
	private State currentState;
	
	public StateMachine() {
		this.network = new Network<>();
		this.nodesByStateName = new HashMap<>();
	}
	
	public Network<State, Agent> getNetwork() {
		return this.network;
	}
	
	public void addState(String stateName) {
		addState(new State(stateName));
	}
	
	public void addState(String stateName, Consumer<Agent> thingToDoWhileInState) {
		addState(new State(stateName, thingToDoWhileInState));
	}

	public void addStates(String... states) {
		for (String state : states) {
			addState(state);
		}
		setCurrentState(states[0]);
	}
	
	public void addState(State state) {
		Node<State> node = new Node<>(state);
		network.addNode(node);
		nodesByStateName.put(state.getStateName(), node);
	}
	
	public void addStates(State... states) {
		for (State state : states) {
			addState(state);
		}
		setCurrentState(states[0]);
	}
	
	public void removeState(String stateName) {
		network.removeNode(nodesByStateName.get(stateName));
	}
	
	public void removeState(State state) {
		network.removeNode(nodesByStateName.get(state.getStateName()));
	}
	
	public Collection<State> getStates() {
		return network.getNodes().stream().map(node -> node.getObject()).collect(Collectors.toList());
	}
	
	public void addTransition(String originStateName, String destinationStateName, Predicate<Agent> check) {
		Edge<State, Agent> edge = new Edge<>(nodesByStateName.get(originStateName), nodesByStateName.get(destinationStateName), check, agent -> {});
		network.addEdge(edge);
	}

	public void addTransition(String originStateName, String destinationStateName, Predicate<Agent> check, Consumer<Agent> todo) {
		Edge<State, Agent> edge = new Edge<>(nodesByStateName.get(originStateName), nodesByStateName.get(destinationStateName), check, todo);
		network.addEdge(edge);
	}
	
	public Collection<Edge<State, Agent>> getTransitionsFrom(String nodeName) {
		return network.getEdgesFrom(nodesByStateName.get(nodeName));
	}
	
	public Collection<Edge<State, Agent>> getTransitionsTo(String nodeName) {
		return network.getEdgesFrom(nodesByStateName.get(nodeName));
	}
	
	public Collection<Edge<State, Agent>> getTransitionsBetween(String nodeName1, String nodeName2) {
		return network.getEdgesBetween(nodesByStateName.get(nodeName1), nodesByStateName.get(nodeName2));
	}

	public void setCurrentState(String stateName) {
		setCurrentState(nodesByStateName.get(stateName).getObject());
	}
	
	public void setCurrentState(State state) {
		this.currentState = state;
	}
	
	public State getCurrentState() {
		return this.currentState;
	}
	
	@Override
	public void executeBehavior(Agent agent) {
		transitionStateIfApplicable(agent);
		currentState.executeBehavior(agent);
	}
	
	private void transitionStateIfApplicable(Agent agent) {
		Collection<Edge<State, Agent>> applicableEdges = getTransitionsFrom(currentState.getStateName()).stream().filter(edge -> edge.applies(agent)).collect(Collectors.toList());
		if (applicableEdges.size() > 0) {
			Edge<State, Agent> transitioningEdge = applicableEdges.iterator().next(); // Pick the first one
			transitioningEdge.doOnEdge(agent);
			this.setCurrentState(transitioningEdge.getDestinationNode().getObject());
		}
	}
}
