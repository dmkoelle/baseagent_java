// filepath: p:/Projects/BaseAgent/baseagent_java/src/org/baseagent/network/MapNetworkRenderer.java
package org.baseagent.network;

import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.util.GeoUtils;
import org.baseagent.worldmap.WorldMapAgent;
import org.baseagent.worldmap.WorldMapGridLayer;
import org.baseagent.worldmap.ui.WorldMapLayerRenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders a Network of Nodes whose Node.object is a MapAgent onto a MapCanvas.
 * Draws straight lines between node pixel positions and small circles for nodes.
 */
public class MapNetworkRenderer<T,R> implements WorldMapLayerRenderer {
    private Network<T, R> network;

    public MapNetworkRenderer(Network<T, R> network) {
        this.network = network;
    }

    @Override
    public void draw(GridCanvasContext gcc, WorldMapGridLayer layer, double canvasWidth, double canvasHeight) {
        Object zoomProp = gcc.getProperties().get("slippyZoom");
        Object offXProp = gcc.getProperties().get("viewOffsetX");
        Object offYProp = gcc.getProperties().get("viewOffsetY");
        Object scaleProp = gcc.getProperties().get("zoomScale");

        int slippyZoom = (zoomProp instanceof Number) ? ((Number)zoomProp).intValue() : 2;
        double viewOffsetX = (offXProp instanceof Number) ? ((Number)offXProp).doubleValue() : 0.0;
        double viewOffsetY = (offYProp instanceof Number) ? ((Number)offYProp).doubleValue() : 0.0;
        double zoomScale = (scaleProp instanceof Number) ? ((Number)scaleProp).doubleValue() : 1.0;

        GraphicsContext g = gcc.getGraphicsContext();
        // Draw edges
        g.setStroke(Color.rgb(30, 144, 255, 0.9)); // DodgerBlue
        g.setLineWidth(2.0);
        for (Edge<T, R> edge : network.getEdges()) {
            T sObj = (edge.getSourceNode() == null) ? null : edge.getSourceNode().getObject();
            T dObj = (edge.getDestinationNode() == null) ? null : edge.getDestinationNode().getObject();
            if (sObj instanceof WorldMapAgent && dObj instanceof WorldMapAgent) {
                WorldMapAgent sa = (WorldMapAgent)sObj;
                WorldMapAgent da = (WorldMapAgent)dObj;
                double[] sp = GeoUtils.latLonToPixelXY(sa.getLatitude(), sa.getLongitude(), slippyZoom, 256);
                double[] dp = GeoUtils.latLonToPixelXY(da.getLatitude(), da.getLongitude(), slippyZoom, 256);
                double sx = sp[0] * zoomScale - viewOffsetX;
                double sy = sp[1] * zoomScale - viewOffsetY;
                double dx = dp[0] * zoomScale - viewOffsetX;
                double dy = dp[1] * zoomScale - viewOffsetY;
                g.strokeLine(sx, sy, dx, dy);
            }
        }

        // Draw nodes
        g.setFill(Color.YELLOW);
        g.setStroke(Color.ORANGE);
        for (Node<T> node : network.getNodes()) {
            T obj = node.getObject();
            if (obj instanceof WorldMapAgent) {
                WorldMapAgent ma = (WorldMapAgent)obj;
                double[] p = GeoUtils.latLonToPixelXY(ma.getLatitude(), ma.getLongitude(), slippyZoom, 256);
                double x = p[0] * zoomScale - viewOffsetX;
                double y = p[1] * zoomScale - viewOffsetY;
                double r = 6.0;
                g.fillOval(x - r, y - r, r*2, r*2);
                g.strokeOval(x - r, y - r, r*2, r*2);
            }
        }
    }
}
