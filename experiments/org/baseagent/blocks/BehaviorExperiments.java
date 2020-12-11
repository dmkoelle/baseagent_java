package org.baseagent.blocks;

public class BehaviorExperiments {
	public static void main(String[] args) {
		Behavior behavior = new BehaviorBuilder()
				.parallel(new BehaviorBuilder().sequential().build())
				.build();
		StateBehavior stateBehavior = new StateBehavior() {
			
		}
		
	}
	
	public Behavior createStateBehavior() {
		// State behaviors should have: startBehavior, executeBehavior, endState
		// onTransitionTo, onTransitionFrom
		// Behavior should also accept parameters, a Map<String, Object>

		Behavior behavior1 = new Behavior1();
		Behavior behavior2 = new Behavior2();
		Behavior behavior3 = new Behavior3();
		
		addState("BEHAVIOR_1", behavior1);
		addState("BEHAVIOR_2", behavior2);
		addState("BEHAVIOR_3", behavior3);
		
		addTransition("BEHAVIOR_1", "BEHAVIOR_2", behavior1.isReadyForState2());
		addTransition("BEHAVIOR_1", "BEHAVIOR_3", behavior1.isReadyForState3());
		addTransition("BEHAVIOR_2", "BEHAVIOR_3", behavior2.isReadyForState3());
		
		setStartState("BEHAVIOR_1");
		addEndState("BEHAVIOR_3", behavior3.isDone());
	}
	
	// So now what's a BLOCK?
	// One thing a BLOCK should do is have a physical manifestation on the platform
	// A BLOCK can also contain logic - e.g., an IF block
	// Essentially, a series of blocks can be written out as a program in another language
	// In this way, BLOCKs can allow for evolution
	// And a BLOCK should have input and output compatibility keys
	
	public Behavior whatsBlock() {
		Block turnOnLight = new Block("TURN_ON_LIGHT");
		
		BlockImplementation.put("TURN_ON_LIGHT", new TurnOnLightFunction());
		
	}
	
	public Behavior createBlockBehavior() {
		
	}
}


