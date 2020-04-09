package org.baseagent.examples.path.traffic;

import org.baseagent.Agent;
import org.baseagent.path.Destination;
import org.baseagent.path.Intersection;
import org.baseagent.path.Segment;
import org.baseagent.path.PathNetwork;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.util.BaseAgentMath;
import org.baseagent.util.BaseAgentUtils;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TrafficApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        Simulation simulation = setupSimulation();
        GridCanvas simulationCanvas = new GridCanvas(simulation, 6, 6);

        primaryStage.setTitle("Traffic Simulation");
        primaryStage.setScene(new Scene(new Group(simulationCanvas), 600, 600));
        primaryStage.show();

        simulation.start();
	}
	
	public Simulation setupSimulation() {
		Simulation simulation = new Simulation(100, 100);
		
		//  Create a network of paths that looks roughly like this:
		//
		//      *---1---*---2---
		//      |       |       \
		//      |       |        \
		//       \      |         |
		//        4     *----3----*
		//         \    |         |
		//          ----*---------*
		//
		// Where * is an intersection
		//
		
		PathNetwork pathNetwork = new PathNetwork();
		Intersection ix1 = new Intersection(10, 10);
		Intersection ix2 = new Intersection(50, 10);
		Intersection ix3 = new Intersection(50, 50);
		Intersection ix4 = new Intersection(100, 50);
		Intersection ix5 = new Intersection(50, 80);
		Intersection ix6 = new Intersection(100, 80);
		Segment path1 = new Segment(ix1, ix2);
		Segment path2 = new Segment(ix2, new Segment.Joint(80, 10), new Segment.Joint(100, 40), ix4);
		Segment path3 = new Segment(ix4, ix6);
		Segment path4 = new Segment(ix1, new Segment.Joint(10, 40), new Segment.Joint(30, 100), ix5);
		Segment path5 = new Segment(ix5, ix6);
		Segment path6 = new Segment(ix2, ix3);
		Segment path7 = new Segment(ix3, ix4);
		Segment path8 = new Segment(ix3, ix5);
		Destination dest1 = new Destination(path1, 0.5, "building 1");
		Destination dest2 = new Destination(path2, 0.25, "apartment 1");
		Destination dest3 = new Destination(path7, 0.50, "apartment 2");
		simulation.add(pathNetwork); // 11/25/19 - New Idea: adding a PathNetwork as the map instead of a Grid
		
		// Create cars that are commuters
		ScriptTemplate workdayCommuteTemplate = new ScriptTemplate(); // 11/25/19 - New Idea: Scriptable behaviors 
		
		for (int i=0; i < 50; i++) {
			String workplace = BaseAgentUtils.sample(); // Must be able to borrow this from? // 11/25/19 - New Idea: Random sampling
			String home = BaseAgentUtils.sample(); // Must be able to borrow this from?
			Script workdayCommute = workdayCommuteTemplate.createInstance(home, workplace); // 11/25/19 - New Idea: A Script that is a Behavior
			CarAgent agent = new CarAgent(); // Actually, an Agent should just be something that has step?
			agent.addBehavior(workdayCommute);
			simulation.addSimulationComponent(agent);
		}
		
		simulation.endWhen(sim -> sim.getStepTime() == 10000);
		simulation.setDelayAfterEachStep(100);

		return simulation;
	}
}
