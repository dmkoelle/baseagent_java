package org.baseagent.examples.explorers;

import java.util.Map;

import org.baseagent.Sensor;
import org.baseagent.behaviors.SwitchingBehavior;
import org.baseagent.behaviors.grid.RandomWanderBehavior;
import org.baseagent.behaviors.grid.WalkToBehavior;
import org.baseagent.comms.Message;
import org.baseagent.embodied.EmbodiedAgent;
import org.baseagent.embodied.effectors.Effector;
import org.baseagent.embodied.effectors.SignalEffector;
import org.baseagent.signals.Signal;

import javafx.scene.paint.Color;

public class Explorer extends EmbodiedAgent {
	private int id;
	private SwitchingBehavior sb;
	private Map<String, Object> simProperties;
	
	public Explorer(int id, Map<String, Object> knowledge) {
		super(3, 3);

		this.id = id;
		this.simProperties = getSimulation().getProperties();
		setKnowledge(knowledge);

		createBody();
		createBehaviors();

		setColor(COLORS[id]);
//		setDrawable(new Drawable() {
//			@Override
//			public void draw(SimulationCanvasContext sc) {
//				VisualizationLibrary.drawTriangleWithHeading2(sc.getGraphicsContext(), getCellX(), getCellY(), sc.getCellWidth(), sc.getCellHeight(), getHeading(), Color.BLACK, Color.BLUE);
//			}
//		});	
	}

	private void createBody() {
		Sensor thingSensor = new SignalSensor((Signal)simProperties.get("THING_SIGNAL"));
		Sensor nearbyAgentSensor = new ProximitySensor((Signal)simProperties.get("AGENT_SIGNAL"), 2);
		place(1, 1, thingSensor);
		place(1, 1, nearbyAgentSensor);
		Effector agentSmellEffector = new SignalEffector((Signal)simProperties.get("AGENT_SIGNAL"));
		Effector grabberEffector = new InventoryEffector((Signal)simProperties.get("THING_SIGNAL"));
		place(1, 1, agentSmellEffector);
		place(1, 1, grabberEffector);
	}
	
	private void createBehaviors() {
		this.sb = new SwitchingBehavior();
		this.addBehavior(sb);

		//  Add switchable behaviors
		sb.addBehavior(MOVEMENT, RANDOM_WANDER, new RandomWanderBehavior(10));
		sb.addBehavior(MOVEMENT, DIRECT, new WalkToBehavior());
		sb.addBehavior(AVOIDANCE, DEFAULT, new AvoidanceBehavior(2));

		// What about a Sensor-Behaviors-Effector combo?
		
		// Set initial behaviors based on knowledge
		setInitialBehaviors();
	}
	
	private void setInitialBehaviors() {
		String role = (String)getKnowledge().get("ROLE");
		switch (role) {
		  case SEEKER : break; 
		}
	}
	
	@Override
	public void onMessageReceived(Message message) {
		// First, see if this agent wants this message because it's a switcher
		if (message.getMessageType().equals(SWITCH_MESSAGE)) {
			
		} else {
			// Otherwise, pass it along!
			super.onMessageReceived(message);
		}
	}
	
	// Movement
	public static final String MOVEMENT = "Movement";
	public static final String RANDOM_WANDER = "Random Wander";
	public static final String DIRECT = "Direct";

	// Knowledge
	public static final String SEEKER = "Seeker";
	
	// Messages
	public static final String SWITCH_MESSAGE = "Switch Message";
	
	// Colors
	public static final Color[] COLORS = new Color[] { Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA };
}
