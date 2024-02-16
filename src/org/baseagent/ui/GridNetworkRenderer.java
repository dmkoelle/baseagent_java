package org.baseagent.ui;

import org.baseagent.grid.GridLayer;
import org.baseagent.network.Edge;
import org.baseagent.network.Network;
import org.baseagent.network.Node;

public abstract class GridNetworkRenderer implements GridLayerRenderer {
	private Network<Node<?>, Edge<?, ?>> network;
	
	public GridNetworkRenderer(Network<Node<?>, Edge<?, ?>> network) {
		this.network = network;
	}
	
	@Override
	public void draw(GridCanvasContext gcc, GridLayer layer, double canvasWidth, double canvasHeight) {
		drawBackground(gcc, canvasWidth, canvasHeight);
		
		for (Node<?> node : network.getNodes()) {
			drawNode(gcc, node);
		}
		
		for (Edge<?, ?> edge : network.getEdges()) {
			drawEdge(gcc, edge);
		}
	}
	
	public abstract void drawBackground(GridCanvasContext gcc, double canvasWidth, double canvasHeight);
	public abstract void drawNode(GridCanvasContext gcc, Node<?> node);
	public abstract void drawEdge(GridCanvasContext gcc, Edge<?, ?> edge);
}
