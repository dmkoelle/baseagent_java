package org.baseagent.network;

public interface NetworkListener {
    void onNodeAdded(Node node);

    void onNodeRemoved(Node node);

    void onEdgeAdded(Edge edge);

    void onEdgeRemoved(Edge edge);
}
