package org.baseagent.embodied;

import java.util.ArrayList;
import java.util.List;

public class ConnectedComponent<T> {
	List<ConnectedComponent<T>> outgoingConnections;
	List<ConnectedComponent<T>> incomingConnections;
	private T inputValue;
	private T outputValue;
	
	public ConnectedComponent() { 
		this.outgoingConnections = new ArrayList<>();
		this.incomingConnections = new ArrayList<>();
	}
	
	public void connectTo(ConnectedComponent<T> c2) {
		this.outgoingConnections.add(c2);
		c2.incomingConnections.add(this);
	}
	
	public void disconnect(ConnectedComponent<T> c2) {
		this.outgoingConnections.remove(c2);
		c2.incomingConnections.remove(this);
	}
	
	public List<ConnectedComponent<T>> getOutgoingConnections() {
		return this.outgoingConnections;
	}
	
	public List<ConnectedComponent<T>> getIncomingConnections() {
		return this.incomingConnections;
	}
	
	public void setInputValue(T value) {
		this.inputValue = value;
	}
	
	public T getInputValue() {
		return this.inputValue;
	};
	
	public void setOutputValue(T value) {
		this.outputValue = value;
		postUpdates();
	}
	
	public T getOutputValue() {
		return this.outputValue;
	};

	private void postUpdates() {
		for (ConnectedComponent<T> cc : getOutgoingConnections()) {
			cc.setInputValue(this.getOutputValue());
		}
	}
}
