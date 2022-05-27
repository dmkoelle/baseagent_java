package org.baseagent.examples.large;

import org.baseagent.grid.Grid;
import org.baseagent.grid.NoPatchGridStepPolicy;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LargeSwarm extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
		int width = 800;
		int height = 600;
		
        Simulation simulation = setupSimulation();
        final GridCanvas canvas = setupSimulationCanvas(simulation, 1, 1); // TODO - this is still a GridCanvas - should be more general?

        primaryStage.setTitle("Large Swarm");
        primaryStage.setScene(new Scene(new Group(canvas), width, height));
        primaryStage.show();

        simulation.start();
	}
	
	protected Simulation setupSimulation() {
		Grid grid = new Grid(800, 600);
		grid.setStepPolicy(new NoPatchGridStepPolicy(grid));
		
		Simulation simulation = new Simulation();
		simulation.setUniverse(grid);
		
		for (int i=0; i < 100; i++) {
			GridAgent flocker = new Flocker(i);
			simulation.add(flocker);
			flocker.placeRandomly();
		}

		simulation.endWhen(sim -> sim.getStepTime() == 5000);
		simulation.setDelayAfterEachStep(50);

		return simulation;
	}
	
	protected GridCanvas setupSimulationCanvas(Simulation simulation, int cellWidth, int cellHeight) {
		GridCanvas canvas = new GridCanvas(simulation, cellWidth, cellHeight);
//		canvas.addGridLayerRenderer("obscurant", new GradientGridCellRenderer(Color.RED, Color.YELLOW));
		return canvas;
	}
	
}
