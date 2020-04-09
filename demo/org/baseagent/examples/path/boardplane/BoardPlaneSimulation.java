package org.baseagent.examples.path.boardplane;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.path.Destination;
import org.baseagent.path.Intersection;
import org.baseagent.path.Segment;
import org.baseagent.path.PathNetwork;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.util.BaseAgentUtils;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BoardPlaneSimulation extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        Simulation simulation = setupSimulation();
        GridCanvas simulationCanvas = new GridCanvas(simulation, 6, 6);

        primaryStage.setTitle("Boarding Plane Simulation");
        primaryStage.setScene(new Scene(new Group(simulationCanvas), 600, 600));
        primaryStage.show();

        simulation.start();
	}
	
	public Simulation setupSimulation() {
		Simulation simulation = new Simulation(100, 100);
		
		//  Create a network of paths that looks roughly like this:
		//
		//           1F  2F  3F  4F  5F  6F  7F  8F  9F 10F 11F 12F 13F 14F 15F 16F 17F 18F 19F 20F 21F 22F 23F 24F
		//           1E  2E  3E  4E  5E  6E  7E  8E  9E 10E 11E 12E 13E 14E 15E 16E 17E 18E 19E 20E 21E 22E 23E 24E
		//           1D  2D  3D  4D  5D  6D  7D  8D  9D 10D 11D 12D 13D 14D 15D 16D 17D 18D 19D 20D 21D 22D 23D 24D
        // QUEUE ----+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+		
		//           1C  2C  3C  4C  5C  6C  7C  8C  9C 10C 11C 12C 13C 14C 15C 16C 17C 18C 19C 20C 21C 22C 23C 24C
		//           1B  2B  3B  4B  5B  6B  7B  8B  9B 10B 11B 12B 13B 14B 15B 16B 17B 18B 19B 20B 21B 22B 23B 24B
		//           1A  2A  3A  4A  5A  6A  7A  8A  9A 10A 11A 12A 13A 14A 15A 16A 17A 18A 19A 20A 21A 22A 23A 24A
		//
		//
		// Where + is an intersection and for you to get to 9A if, say, there's someone in 9C, they need to move
		//
		
		PathNetwork pathNetwork = new PathNetwork();
		List<Segment> aisleSegments = Segment.createStraightPathsWithNIntersectionsAndSpacing(24, 10, 0, 40, 240, 40); // n, spacing, x1, y1, x2, y2
				
		for (int row=1; row <= 24; row++) {
			Intersection ix = aisleSegments.get(row-1).getEnd();
			Segment leftPath = new Segment(ix, new Segment.Endpoint(row*10, 10));
			Segment rightPath = new Segment(ix, new Segment.Endpoint(row*10, 70));
			for (int seat=1; seat <= 3; seat++) {
				new Destination(rightPath, 1.0-(0.3 * seat), row+""+(char)(seat+64));
			}
			for (int seat=4; seat <= 6; seat++) {
				new Destination(leftPath, 0.3 * (seat-3.0), row+""+(char)(seat+64));
			}
		}
		simulation.add(pathNetwork); // 11/25/19 - New Idea: adding a PathNetwork as the map instead of a Grid
		
		List<PassengerAgent> passengers = new ArrayList<>();
		for (Destination destination : pathNetwork.getAllDestinations()) {
			PassengerAgent passenger = new PassengerAgent(destination);
			passengers.add(passenger);
			simulation.addSimulationComponent(passenger);
		}

        List<PassengerAgent> boardingSequence = createBoardingSequence(passengers);
        

		simulation.endWhen(sim -> sim.getStepTime() == 10000); // Actually, end when all passengers are boarded
		simulation.setDelayAfterEachStep(100);

		return simulation;
	}
	
	/**
	 * This sequence splits the rows into three boarding zones
	 */
	private List<PassengerAgent> createBoardingSequence(List<PassengerAgent> passengers) {
		passengers = BaseAgentUtils.shuffle(passengers);
		
		List<PassengerAgent> zone1 = new ArrayList<>();
		List<PassengerAgent> zone2 = new ArrayList<>();
		List<PassengerAgent> zone3 = new ArrayList<>();
		
		for (PassengerAgent passenger : passengers) {
			if (passenger.getRow() > 16) {
				zone1.add(passenger);
			} 
			else if ((passenger.getRow() > 8) && (passenger.getRow() <= 16)) {
				zone2.add(passenger);
			}
			else {
				zone3.add(passenger);
			}
		}
		
		List<PassengerAgent> returnList = new ArrayList<>();
		returnList.addAll(zone3);
		returnList.addAll(zone2);
		returnList.addAll(zone1);
		return returnList;
	}
}
