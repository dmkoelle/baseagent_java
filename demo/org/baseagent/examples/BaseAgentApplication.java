package org.baseagent.examples;

import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class BaseAgentApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
		int width = 500;
		int height = 500;
		
        Simulation simulation = setupSimulation();
        final GridCanvas canvas = setupSimulationCanvas(simulation, 5, 5); // TODO - this is still a GridCanvas - should be more general?

        primaryStage.setTitle(getTitle());
        primaryStage.setScene(new Scene(new Group(canvas), width, height));
        primaryStage.show();

        simulation.start();
	}
	
	protected abstract String getTitle();
	
	protected abstract Simulation setupSimulation();

	protected abstract GridCanvas setupSimulationCanvas(Simulation simulation, int width, int height);

}
