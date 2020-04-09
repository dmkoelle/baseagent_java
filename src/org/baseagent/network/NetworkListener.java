package org.baseagent.network;

public interface NetworkListener {
	public void onNodeAdded(Node node);
	public void onNodeRemoved(Node node);
	public void onEdgeAdded(Edge edge);
	public void onEdgeRemoved(Edge edge);
}
