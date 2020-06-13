package org.baseagent.examples.embodied.simple;

import org.baseagent.behaviors.grid.RandomWanderBehavior;
import org.baseagent.embodied.EmbodiedAgent;
import org.baseagent.grid.Grid;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.ui.GridCanvasContext;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimpleEmbodiedApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
		int width = 800;
		int height = 600;
		
        Simulation simulation = setupSimulation();
        final GridCanvas canvas = setupSimulationCanvas(simulation, 5, 5); 

        primaryStage.setTitle("Simple Embodied");
        primaryStage.setScene(new Scene(new Group(canvas), width, height));
        primaryStage.show();

        simulation.start();
	}
	
	protected Simulation setupSimulation() {
		Grid grid = new Grid(160, 120);
		
		Simulation simulation = new Simulation();
		simulation.setUniverse(grid);

		SimpleEmbodiedAgent agent = new SimpleEmbodiedAgent();
		
		simulation.addSimulationComponent(agent);
		agent.placeRandomly();

		// Place some random things of interest
		
		
		
		// Simulation settings
		simulation.endWhen(sim -> sim.getStepTime() == 5000);
		simulation.setDelayAfterEachStep(50);

		return simulation;
	}
	
	protected GridCanvas setupSimulationCanvas(Simulation simulation, int cellWidth, int cellHeight) {
		GridCanvas canvas = new GridCanvas(simulation, cellWidth, cellHeight);
		return canvas;
	}

}

class SimpleEmbodiedAgent extends EmbodiedAgent {
	public SimpleEmbodiedAgent() {
		super(3, 3);
		setColor(Color.BLUEVIOLET);
		addBehavior(new RandomWanderBehavior(5));
		addBehavior(agent -> System.out.println(((EmbodiedAgent)agent).getCellX()+", "+((EmbodiedAgent)agent).getCellY()));
	}
	

}
