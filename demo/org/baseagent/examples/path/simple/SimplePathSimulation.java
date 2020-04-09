package org.baseagent.examples.path.simple;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.network.Network;
import org.baseagent.path.Intersection;
import org.baseagent.path.PathComponent;
import org.baseagent.path.Segment;
import org.baseagent.path.Sink;
import org.baseagent.path.Source;
import org.baseagent.sim.PathAgent;
import org.baseagent.sim.PathMessageDrawableSimulationComponent;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimplePathSimulation extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        Simulation simulation = setupSimulation();
        GridCanvas simulationCanvas = new GridCanvas(simulation, 10, 10);

        primaryStage.setTitle("Simple Path Simulation");
        primaryStage.setScene(new Scene(new Group(simulationCanvas), 100, 100));
        primaryStage.show();

        simulation.start();
	}
	
	public Simulation setupSimulation() {
		Simulation simulation = new Simulation(100, 100);
		
		//  Create a network of paths that looks roughly like this:
		//
		//                  BLUE_SINK
		//                      |
		//                      |
		//          SOURCE -----+-----  RED_SINK
		//                      |
		//                      |
		//                  GREEN_SINK
		//
		//
		// Where + is an intersection and for you to get to 9A if, say, there's someone in 9C, they need to move
		//
		
		Network<Segment, PathComponent> pathNetwork = new Network();
		Source agentSource = new Source(0, 50); 
		Sink blueSink = new Sink(50, 0);
		Sink redSink = new Sink(100, 50);
		Sink greenSink = new Sink(50, 100);
		simulation.addSource(agentSource);

		Intersection ix = new Intersection(50, 50);

		Segment allPath = Segment.createSegment(pathNetwork, agentSource, ix);
		Segment bluePath = Segment.createSegment(pathNetwork, ix, blueSink);
		Segment redPath = Segment.createSegment(pathNetwork, ix, redSink);
		Segment greenPath = Segment.createSegment(pathNetwork, ix, greenSink);

		ix.addIntersectionAction(pathAgent -> {
			if (pathAgent.getKnowledge().get("COLOR").equals("RED")) {
				pathAgent.setNextSegment(redPath);
			}
			else if (pathAgent.getKnowledge().get("COLOR").equals("BLUE")) {
				pathAgent.setNextSegment(bluePath);
			}
			else if (pathAgent.getKnowledge().get("COLOR").equals("GREEN")) {
				pathAgent.setNextSegment(greenPath);
			}
		});

		simulation.add(pathNetwork); // 11/25/19 - New Idea: adding a PathNetwork as the map instead of a Grid

		int NUM_AGENTS = 100;
		List<PathAgent> agents = new ArrayList<>();
		for (int i=0; i < NUM_AGENTS; i++) {
			PathAgent agent = new PathAgent();
			agent.getKnowledge().put("COLOR", "RED");
		}
		agentSource.setList(agents);

		simulation.endWhen(sim -> agentSource.getList().isEmpty()); // Actually, end when all passengers are boarded
		simulation.setDelayAfterEachStep(100);

		return simulation;
	}
	
}
