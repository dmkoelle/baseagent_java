package org.baseagent.network;

import java.util.Map;

public class Node<T> {
	private T object;
	private Map<String, Object> payload;
	
	public Node(T object) {
		this.object = object;
	}
	
	public T getObject() {
		return this.object;
	}
	
	@Override
	public String toString() {
		return "Node("+object.toString()+")";
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof Node)) return false;
		Node<T> n2 = (Node<T>)o;
		
		return this.getObject().equals(n2.getObject());
	}
	
	@Override
	public int hashCode() {
		return this.getObject().hashCode();
	}
}
