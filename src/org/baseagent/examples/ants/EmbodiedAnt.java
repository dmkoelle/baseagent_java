package org.baseagent.examples.ants;

import java.util.Random;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.embodied.EmbodiedAgent;
import org.baseagent.embodied.effectors.ForceEffector;
import org.baseagent.embodied.sensors.EmbodiedPheromoneSensor;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridAgent;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridPosition;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.grid.ui.GridDrawable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * Ant implemented as an EmbodiedAgent with two pheromone sensors (left/right).
 */
public class EmbodiedAnt extends EmbodiedAgent {
    private boolean carrying = false;
    private GridPosition nest;
    private String foodLayer = "food";
    private String wallLayer = "walls";
    private String pheromoneLayer = "pheromone";
    private static final Random RAND = new Random();

    public EmbodiedAnt(int nestX, int nestY) {
        super(4, 1); // body width 4, height 1 (left, processor slot, effector, right)
        this.nest = new GridPosition(nestX, nestY);
        setColor(Color.DARKBLUE);

        // random heading
        rotateTo(RAND.nextDouble() * Math.PI * 2.0);

        // Custom drawable: draw multi-segment ant using fine position and heading
        setDrawable(new GridDrawable() {
            @Override
            public void draw(GridCanvasContext gcc) {
                GraphicsContext gc = gcc.getGraphicsContext();
                double worldX = getFineX() * gcc.getXFactor();
                double worldY = getFineY() * gcc.getYFactor();
                double cellW = gcc.getCellWidth() * gcc.getZoom();
                double cellH = gcc.getCellHeight() * gcc.getZoom();

                // center for drawing
                double cx = worldX;
                double cy = worldY;

                gc.save();
                Rotate r = new Rotate(Math.toDegrees(getHeading()), cx + cellW/2.0, cy + cellH/2.0);
                gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

                // scale factor for segments
                double segW = Math.max(2.0, cellW * 0.45);
                double segH = Math.max(2.0, cellH * 0.45);

                // positions along the heading axis (back to front)
                double midX = cx + cellW/2.0;
                double midY = cy + cellH/2.0;

                double backOffset = -segW * 0.9;
                double midOffset = 0.0;
                double frontOffset = segW * 0.9;

                // draw abdomen (back)
                gc.setFill(Color.BLACK);
                gc.fillOval(midX + backOffset - segW/2.0, midY - segH/2.0, segW, segH);
                // thorax (middle)
                gc.fillOval(midX + midOffset - segW/2.0, midY - segH/2.0, segW * 0.9, segH * 0.9);
                // head (front)
                gc.fillOval(midX + frontOffset - segW/3.0, midY - segH/3.0, segW * 0.6, segH * 0.6);

                gc.setStroke(Color.DARKGRAY);
                gc.setLineWidth(Math.max(1.0, cellW * 0.06));

                // legs: three on each side (relative offsets)
                for (int i = -1; i <= 1; i++) {
                    double legY = midY + i * segH * 0.35;
                    // left leg
                    gc.strokeLine(midX - segW*0.2, legY, midX - segW*0.9, legY + segH*0.4);
                    // right leg
                    gc.strokeLine(midX + segW*0.2, legY, midX + segW*0.9, legY + segH*0.4);
                }

                // antennae from head
                gc.strokeLine(midX + frontOffset + segW*0.2, midY - segH*0.3, midX + frontOffset + segW*0.8, midY - segH);
                gc.strokeLine(midX + frontOffset + segW*0.2, midY + segH*0.05, midX + frontOffset + segW*0.8, midY + segH*0.4);

                gc.restore();
            }
        });

        // place two pheromone sensors at left and right body cells
        final EmbodiedPheromoneSensor left = new EmbodiedPheromoneSensor(pheromoneLayer, 0, 0);
        final EmbodiedPheromoneSensor right = new EmbodiedPheromoneSensor(pheromoneLayer, 3, 0);
        incorporate(left);
        incorporate(right);

        // place a force effector (visualized in body) but movement is driven by behavior forces
        final ForceEffector force = new ForceEffector(pheromoneLayer);
        force.setCellX(2);
        force.setCellY(0);
        incorporate(force);

        // Sample debug printing for sensors
        addBehavior(new Behavior() {
            @Override
            public void executeBehavior(Agent agent) {
                if ((agent.getSimpleID() % 30) != 0) return;
                Double l = left.getIntensityPort().getOutputValue();
                Double r = right.getIntensityPort().getOutputValue();
                if (l == null) l = 0.0; if (r == null) r = 0.0;
                System.out.println("Ant#" + agent.getSimpleID() + " sensors L=" + String.format("%.2f", l) + " R=" + String.format("%.2f", r) +
                        " pos=" + ((GridAgent)agent).getCellX() + "," + ((GridAgent)agent).getCellY());
            }
        });

        // Main behavior: pickup/search/carry/return/wander using addForce for smooth movement
        addBehavior(new Behavior() {
            @Override
            public void executeBehavior(Agent agent) {
                EmbodiedAnt ant = (EmbodiedAnt) agent;
                Grid grid = (Grid) ant.getSimulation().getUniverse();

                // If carrying food: deposit pheromone and add force toward nest
                if (ant.carrying) {
                    GridLayer<Double> pher = grid.getGridLayer(pheromoneLayer);
                    Double val = (Double)pher.current().get(ant.getCellX(), ant.getCellY());
                    if (val == null) val = 0.0;
                    pher.set(ant.getCellX(), ant.getCellY(), Math.min(6.0, val + 0.6));
                    double dirToNest = org.baseagent.util.BaseAgentMath.direction(ant, ant.nest);
                    ant.addForce(new org.baseagent.util.Vector2D(0.06, dirToNest));
                    if (ant.isAt(ant.nest)) {
                        ant.carrying = false; // drop food at nest
                    }
                    return;
                }

                // Pick up food on current cell
                Object foodHere = grid.getGridLayer(foodLayer).current().get(ant.getCellX(), ant.getCellY());
                if (foodHere != null) {
                    grid.getGridLayer(foodLayer).set(ant.getCellX(), ant.getCellY(), null);
                    ant.carrying = true;
                    return;
                }

                // Search for nearest food within radius and apply force toward it
                int searchRadius = 12;
                int foundX = -1, foundY = -1;
                outer: for (int radius = 1; radius <= searchRadius; radius++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        for (int dy = -radius; dy <= radius; dy++) {
                            int nx = ant.getCellX() + dx;
                            int ny = ant.getCellY() + dy;
                            if (!grid.isValidPosition(nx, ny)) continue;
                            if (grid.getGridLayer(foodLayer).current().get(nx, ny) != null) {
                                foundX = nx; foundY = ny; break outer;
                            }
                        }
                    }
                }
                if (foundX >= 0) {
                    double dirToFood = org.baseagent.util.BaseAgentMath.direction(ant, new org.baseagent.grid.GridPosition(foundX, foundY));
                    ant.addForce(new org.baseagent.util.Vector2D(0.05, dirToFood));
                    return;
                }

                // If pheromone present, bias toward it using left/right sensors
                double leftVal = left.getIntensityPort().getOutputValue() == null ? 0.0 : left.getIntensityPort().getOutputValue();
                double rightVal = right.getIntensityPort().getOutputValue() == null ? 0.0 : right.getIntensityPort().getOutputValue();
                double total = leftVal + rightVal;
                if (total > 0.0001) {
                    double steer = Math.signum(rightVal - leftVal) * 0.25;
                    double desired = ant.getHeading() + steer;
                    ant.addForce(new org.baseagent.util.Vector2D(0.04, desired));
                    return;
                }

                // avoid walls: if on wall, nudge random
                Object wall = grid.getGridLayer(wallLayer).current().get(ant.getCellX(), ant.getCellY());
                if (wall != null) {
                    ant.moveRandomly(1);
                    return;
                }

                // fallback wander
                double jitter = (RAND.nextDouble() - 0.5) * 0.4;
                ant.addForce(new org.baseagent.util.Vector2D(0.03, ant.getHeading() + jitter));
            }
        });
    }
}
