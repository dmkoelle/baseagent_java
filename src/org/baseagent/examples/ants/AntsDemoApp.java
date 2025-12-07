package org.baseagent.examples.ants;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.grid.ui.GridCanvasForSimulation;
import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.grid.ui.GridCellRenderer;
import org.baseagent.sim.Simulation;

public class AntsDemoApp extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Simulation simulation = new Simulation();
        simulation.endWhen(sim -> sim.getStepTime() == 40000);
        simulation.setDelayAfterEachStep(200);

        // create a grid
        int w = 120, h = 80;
        Grid grid = new Grid(w, h);
        simulation.setUniverse(grid);

        // layers
        GridLayer<Object> food = grid.createGridLayer("food", GridLayerUpdateOption.NO_SWITCH);
        GridLayer<Object> walls = grid.createGridLayer("walls", GridLayerUpdateOption.NO_SWITCH);
        GridLayer<Double> pher = grid.createGridLayer("pheromone", GridLayerUpdateOption.NEXT_BECOMES_CURRENT);
        // initialize pheromone to zeros
        pher.fill(0.0);

        // add pheromone diffusion patch
        simulation.add(new PheromonePatch(0.25, 0.01));

        // create canvas tied to simulation so beacons/agents are drawn
        GridCanvasForSimulation canvas = new GridCanvasForSimulation(simulation, grid, 8, 8, 1, 1);

        // renderers: food
        canvas.addGridLayerRenderer("food", new GridCellRenderer() {
            @Override
            public void drawCell(GridCanvasContext gcc, GridLayer layer, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
                if (value == null) return;
                gcc.getGraphicsContext().setFill(Color.GREEN);
                gcc.getGraphicsContext().fillOval(xInPixels + widthInPixels*0.1, yInPixels + heightInPixels*0.1, widthInPixels*0.8, heightInPixels*0.8);
            }
        });

        // walls
        canvas.addGridLayerRenderer("walls", new GridCellRenderer() {
            @Override
            public void drawCell(GridCanvasContext gcc, GridLayer layer, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
                if (value == null) return;
                gcc.getGraphicsContext().setFill(Color.DARKGRAY);
                gcc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
            }
        });

        // pheromone heatmap renderer
        canvas.addGridLayerRenderer("pheromone", new GridCellRenderer() {
            @Override
            public void drawCell(GridCanvasContext gcc, GridLayer layer, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
                if (value == null) return;
                double val = 0.0;
                if (value instanceof Double) val = (Double) value;
                if (val <= 0.0) return;
                double alpha = Math.min(1.0, val / 6.0);
                javafx.scene.paint.Color c = Color.rgb(255, 200, 0, alpha);
                gcc.getGraphicsContext().setFill(c);
                gcc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
            }
        });

        // Mouse interactions: left-click to place food, shift+left to place wall, right-click to clear
        canvas.setOnCellClicked(ev -> {
            int cx = ev.getCellX(), cy = ev.getCellY();
            if (ev.getButton() == MouseButton.PRIMARY) {
                if (ev.isShiftDown()) {
                    walls.set(cx, cy, "W");
                } else {
                    food.set(cx, cy, "F");
                }
            } else if (ev.getButton() == MouseButton.SECONDARY) {
                food.clear(cx, cy);
                walls.clear(cx, cy);
            }
        });

        // Spawn nest and ants
        int nestX = w/2, nestY = h/2;
        // place some food for demonstration
        for (int i = 0; i < 40; i++) {
            int fx = (int)(Math.random() * w);
            int fy = (int)(Math.random() * h);
            food.set(fx, fy, "F");
        }

        for (int i = 0; i < 120; i++) {
            EmbodiedAnt a = new EmbodiedAnt(nestX, nestY);
            simulation.add(a);
            a.placeAt(nestX + (int)((Math.random()-0.5)*10), nestY + (int)((Math.random()-0.5)*10));
        }

        BorderPane root = new BorderPane();
        ToolBar tb = new ToolBar(new Label("Left-click: food, Shift+Left: wall, Right-click: clear"));
        root.setTop(tb);
        root.setCenter(canvas);

        primaryStage.setTitle("Ants Demo");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(1100);
        primaryStage.setHeight(800);
        primaryStage.show();

        simulation.start();
    }
}