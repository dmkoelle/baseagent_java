package org.baseagent.examples.template;

import org.baseagent.Agent;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TemplateApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        Simulation simulation = setupSimulation();
        GridCanvas simulationCanvas = new GridCanvas(simulation, 6, 6);

        primaryStage.setTitle("ARQ");
        primaryStage.setScene(new Scene(new Group(simulationCanvas), 600, 600));
        primaryStage.show();

        simulation.start();
	}
	
	public Simulation setupSimulation() {
		Simulation simulation = new Simulation(100, 100);
		
		for (int i=0; i < 100; i++) {
			Agent agent = new TemplateAgent("agents");
			simulation.addSimulationComponent(agent);
			agent.placeRandomly();
		}
		
		simulation.endWhen(sim -> sim.getStepTime() == 10000);
		simulation.setDelayAfterEachStep(100);

		return simulation;
	}
}
