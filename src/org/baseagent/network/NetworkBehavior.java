package org.baseagent.network;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;

public class NetworkBehavior<T, R> extends Network<T, R> implements Behavior {
	private Node<T> currentNode;
	
	public NetworkBehavior() {
		super();
	}
	
	public Node<T> getCurrentNode() {
		return this.currentNode;
	}
	
	public void setCurrentNode(Node<T> node) {
		this.currentNode = node;
	}

	@Override
	public void executeBehavior(Agent agent) {
		if ((this.currentNode != null) && (this.currentNode instanceof Behavior)) {
			((Behavior)this.currentNode).executeBehavior(agent);
		}
	}
}
