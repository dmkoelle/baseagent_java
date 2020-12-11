package org.baseagent.grid;

import org.baseagent.behaviors.grid.RandomWanderBehavior;
import org.baseagent.behaviors.grid.WalkToBehavior;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SmoothTravelTest extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		int cellWidth = 5;
		int cellHeight = 5;
		int cellXSpacing = 0;
		int cellYSpacing = 0;
		int canvasWidth = 500 * (cellWidth + cellXSpacing);
		int canvasHeight = 500 * (cellHeight + cellXSpacing);
		
		Simulation sim = new Simulation();
		Grid grid = new Grid(200, 200);
		sim.setUniverse(grid);
		
		for (int i=0; i < 500; i++) {
			GridAgent agent = new GridAgent();
			sim.addSimulationComponent(agent);
			agent.moveTo(100, 100);
	//		WalkToBehavior walkTo = new WalkToBehavior(200, 100, 0.2);
			RandomWanderBehavior walkTo = new RandomWanderBehavior(20, 2.3);
			agent.addBehavior(walkTo);
		}
		
		sim.endWhen(s -> s.getStepTime() > 10000);
		sim.setDelayAfterEachStep(100);
		
		GridCanvas canvas = new GridCanvas(sim, grid, cellWidth, cellHeight);
//		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, m -> walkTo.setDestination((int)(m.getSceneX() / cellWidth),  (int)(m.getSceneY() / cellHeight)));
		stage.setTitle("BaseAgent Test");
		stage.setScene(new Scene(new ScrollPane(canvas), canvas.getWidth(), canvas.getHeight()));
		stage.show();
		
		sim.start();
	}
}
