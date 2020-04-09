package org.baseagent.examples.ui;

import java.awt.Graphics2D;

import org.baseagent.Agent;
import org.baseagent.comms.Message;
import org.baseagent.embodied.Processor;
import org.baseagent.embodied.effectors.EmbodiedEffector;
import org.baseagent.embodied.sensors.EmbodiedSensor;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.Drawable;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.stage.Stage;

public class UiVehicles extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		Simulation simulation = new Simulation(100, 100);
		simulation.scatter(Vehicle.class, 10);
		simulation.place("light", 50, 50);
		
		GridCanvas canvas = new GridCanvas(simulation);
		
	}
	
	class Vehicle extends Agent implements Drawable {
		public Vehicle() {
			super();
			
			// Set up the sensors and effectors, and connect them together
			EmbodiedSensor lightSensor1 = new EmbodiedSensor("light");
			EmbodiedSensor lightSensor2 = new EmbodiedSensor("light");			
			EmbodiedEffector wheel1 = new EmbodiedEffector("wheel");
			EmbodiedEffector wheel2 = new EmbodiedEffector("wheel");
			lightSensor1.connectTo(wheel2, d -> d);
			lightSensor2.connectTo(wheel2, d -> d);
			Processor p = new NeuralNetworkProcessor();
			lightSensor1.connectTo(p);
			lightSensor1.connectTo(p);
			
			// Later, we can add a proximity sensor
			EmbodiedSensor proximity = new EmbodiedSensor("proximity"); // How does this know about proximity - and to what?
			Connector conn3 = new Connector(proximity, wheel1, d -> -d); // Suppress the wheel
			Connector conn4 = new Connector(proximity, wheel2, d -> -d); // Suppress the wheel
			
			// Put the sensors on the body
			setupBody(3, 3);
			body().place(lightSensor1, 0, 0);
			body().place(lightSensor2, 2, 0);
			body().place(wheel1, 2, 0);
			body().place(wheel2, 2, 2);
			
			// And later...
			body().place(proximity, 1, 0);
		}
		
		@Override
		public void step() {
			body().step();
		}
		
		@Override
		public void onMessageReceived(Message message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void draw(Graphics2D graphics, int width, int height) {
			
		}
	}
}
