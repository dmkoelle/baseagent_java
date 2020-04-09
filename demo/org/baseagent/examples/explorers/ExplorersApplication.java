package org.baseagent.examples.explorers;

import java.util.List;

import org.baseagent.examples.large.Flocker;
import org.baseagent.grid.Grid;
import org.baseagent.grid.NoPatchGridStepPolicy;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ExplorersApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
		int width = 800;
		int height = 600;
		
        Simulation simulation = setupSimulation();
        final GridCanvas canvas = setupSimulationCanvas(simulation, 5, 5); // TODO - this is still a GridCanvas - should be more general?

        primaryStage.setTitle("Explorers");
        primaryStage.setScene(new Scene(new Group(canvas), width, height));
        primaryStage.show();

        simulation.start();
	}
	
	protected Simulation setupSimulation() {
		Grid grid = new Grid(160, 120);
		
		Simulation simulation = new Simulation();
		simulation.setUniverse(grid);

		// Place the Explorers. Each has a subset of sensors and effectors.
		List<Explorer> explorers = new ArrayList<>();
		explorers.add(new RedExplorer());
		explorers.add(new BlueExplorer());
		explorers.add(new GreenExplorer());
		explorers.add(new YellowExplorer());
		explorers.add(new MagentaExplorer());
		
		for (int i=0; i < 5; i++) {
			simulation.addSimulationComponent(explorers.get(i));
			explorers.get(i).placeRandomly();
		}

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
