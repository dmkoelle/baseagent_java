package org.baseagent.examples.vehicles;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.Beacon;
import org.baseagent.grid.Grid;
import org.baseagent.signals.Signal;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.defaults.VisualizationLibrary;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class VehiclesApplication extends Application {
	private Beacon light;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        Simulation simulation = setupSimulation();
        GridCanvas simulationCanvas = new GridCanvas(simulation, 40, 40);
        simulationCanvas.setOnMouseClicked(e -> moveLight(simulationCanvas.getGridCanvasContext(), e.getX(), e.getY()));

        primaryStage.setTitle("Valentino Braitenberg's \"Vehicles\"");
        primaryStage.setScene(new Scene(new Group(simulationCanvas), 1200, 1200));
        primaryStage.show();

        simulation.start();
	}
	
	public Simulation setupSimulation() {
		Simulation simulation = new Simulation(new Grid(30, 30));

		doSimulation1(simulation);
		
		simulation.endWhen(sim -> sim.getStepTime() == 10000);
		simulation.setDelayAfterEachStep(100);

		return simulation;
	}
	
	/** In Simulation 1, we'll set up a vehicle that avoids the light */
	public void doSimulation1(Simulation simulation) {
		Signal lightSignal = new Signal("light");
		this.light = new LightBeacon("lightLayer", lightSignal, 20, 20);
		simulation.add(light);

		List<GridAgent> agents = new ArrayList<>();
		agents.add(new VehiclesAgent("vehicleLayer", Color.LIGHTSALMON, lightSignal, VehiclesAgent.ATTRACTED_TO_LIGHT));
		agents.add(new VehiclesAgent("vehicleLayer", Color.LIGHTSALMON, lightSignal, VehiclesAgent.ATTRACTED_TO_LIGHT));
		agents.add(new VehiclesAgent("vehicleLayer", Color.AZURE, lightSignal, VehiclesAgent.SCARED_OF_LIGHT));
		agents.add(new VehiclesAgent("vehicleLayer", Color.AZURE, lightSignal, VehiclesAgent.SCARED_OF_LIGHT));
		
		for (GridAgent agent : agents) {
			simulation.add(agent);
			agent.placeRandomly();
		}
	}

	private void moveLight(GridCanvasContext scc, double graphicX, double graphicY) {
		int gridX = VisualizationLibrary.getCellXForGraphicX(scc, graphicX);
		int gridY = VisualizationLibrary.getCellXForGraphicX(scc, graphicX);
		light.setCellX(gridX); light.setCellY(gridY);
	}
}
