package org.baseagent.embodied.sensors;

import org.baseagent.Agent;
import org.baseagent.embodied.ConnectedComponent;
import org.baseagent.embodied.EmbodiedAgent;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridAgent;
import org.baseagent.grid.GridLayer;

/**
 * A simple embodied pheromone sensor mounted in the body grid. The sensor's
 * cellX/cellY are body-local coordinates; sense(agent) computes the world cell
 * under that sensor by rotating around the body center using the agent heading.
 */
public class EmbodiedPheromoneSensor extends EmbodiedSensor {
    private double lastValue = 0.0;
    private ConnectedComponent<Double> intensityPort;

    public EmbodiedPheromoneSensor(String layerName, int bodyCellX, int bodyCellY) {
        super(layerName);
        setCellX(bodyCellX);
        setCellY(bodyCellY);
        this.intensityPort = new ConnectedComponent<>();
    }

    @Override
    public void sense(Agent agent) {
        if (!(agent instanceof GridAgent)) return;
        GridAgent ga = (GridAgent) agent;
        Grid grid = (Grid) ga.getSimulation().getUniverse();

        // Determine body center
        if (!(agent instanceof EmbodiedAgent)) {
            // Fallback: sample at agent location
            Object o = grid.getGridLayer(getLayerName()).current().get(ga.getCellX(), ga.getCellY());
            if (o instanceof Double) lastValue = (Double)o; else lastValue = 0.0;
            this.intensityPort.setOutputValue(lastValue);
            return;
        }

        EmbodiedAgent ea = (EmbodiedAgent) agent;
        Grid body = ea.getBody();
        int bw = body.getWidthInCells();
        int bh = body.getHeightInCells();
        double centerX = (bw - 1) / 2.0;
        double centerY = (bh - 1) / 2.0;

        double localX = getCellX() - centerX;
        double localY = getCellY() - centerY;

        double h = ea.getHeading();
        double rotX = localX * Math.cos(h) - localY * Math.sin(h);
        double rotY = localX * Math.sin(h) + localY * Math.cos(h);

        int worldX = ga.getCellX() + (int)Math.round(rotX);
        int worldY = ga.getCellY() + (int)Math.round(rotY);

        // Read pheromone value at world cell
        GridLayer<?> layer = grid.getGridLayer(getLayerName());
        if (layer == null) {
            lastValue = 0.0;
            this.intensityPort.setOutputValue(lastValue);
            return;
        }
        Object o = layer.current().get(worldX, worldY);
        if (o instanceof Double) lastValue = (Double)o; else lastValue = 0.0;
        this.intensityPort.setOutputValue(lastValue);
    }

    public double getLastValue() { return lastValue; }

    public ConnectedComponent<Double> getIntensityPort() { return this.intensityPort; }
}