package org.baseagent.network;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.baseagent.grid.GridLayer;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.GridLayerRenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Utility class to load road network data from a formatted text file
 * and create a Network instance representing the road connections.
 */
public class GridNetworkRenderer<T,R> implements GridLayerRenderer {
    private Network<T, R> network;

    public GridNetworkRenderer(Network<T, R> network) {
        this.network = network;
    }

    private BiConsumer<GridCanvasContext, Network<T, R>> graphRenderer = (gcc, network) -> { };
    
    private BiConsumer<GridCanvasContext, Node<T>> nodeRenderer = (gcc, node) -> {
        Optional<HasGridPosition> posop = GridNetworkMath.getPossibleGridPositionForNode(node);
        if (posop.isPresent()) {
            GraphicsContext g = gcc.getGraphicsContext();
            g.setFill(Color.MAGENTA);
            g.setStroke(Color.DARKBLUE);
            g.fillOval(posop.get().getCellX() * gcc.getCellWidth(), posop.get().getCellY() * gcc.getCellHeight(), gcc.getCellWidth(), gcc.getCellHeight());
        }
    };
    
    private BiConsumer<GridCanvasContext, Edge<T, R>> edgeRenderer = (gcc, edge) -> { 
        Optional<HasGridPosition> sourcePosop = GridNetworkMath.getPossibleGridPositionForNode(edge.getSourceNode());
        Optional<HasGridPosition> destinationPosop = GridNetworkMath.getPossibleGridPositionForNode(edge.getDestinationNode());

        if (sourcePosop.isPresent() && destinationPosop.isPresent()) {
            GraphicsContext g = gcc.getGraphicsContext();
            g.setStroke(Color.LIGHTBLUE);
            g.strokeLine(sourcePosop.get().getCellX() * gcc.getCellWidth(), sourcePosop.get().getCellY() * gcc.getCellHeight(), destinationPosop.get().getCellX() * gcc.getCellWidth(), destinationPosop.get().getCellY() * gcc.getCellHeight());
        }
    };
    
    public void setGraphRenderer(BiConsumer<GridCanvasContext, Network<T, R>> graphRenderer) {
        this.graphRenderer = graphRenderer;
    }

    public void setNodeRenderer(BiConsumer<GridCanvasContext, Node<T>> nodeRenderer) {
        this.nodeRenderer = nodeRenderer;
    }
    
    public void setEdgeRenderer(BiConsumer<GridCanvasContext, Edge<T, R>> edgeRenderer) {
        this.edgeRenderer = edgeRenderer;
    }
    
    @Override
    public void draw(GridCanvasContext gcc, GridLayer layer, double canvasWidth, double canvasHeight) {
        graphRenderer.accept(gcc, network);
        
        for (Edge<T, R> edge : network.getEdges()) {
            edgeRenderer.accept(gcc, edge);
        }
        
        for (Node<T> node : network.getNodes()) {
            nodeRenderer.accept(gcc, node);
        }
    }
}