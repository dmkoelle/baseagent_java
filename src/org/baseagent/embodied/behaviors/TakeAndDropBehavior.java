package org.baseagent.embodied.behaviors;

import org.baseagent.Agent;
import org.baseagent.behaviors.LifecycleBehavior;
import org.baseagent.comms.Message;
import org.baseagent.comms.MessageListener;
import org.baseagent.embodied.EmbodiedAgent;

public class TakeAndDropBehavior extends LifecycleBehavior implements MessageListener {
	public TakeAndDropBehavior(EmbodiedAgent agent) {
		// TODO - Whoo boy, I don't know about this EmbodiedFullStackBehavior deal. I could come up with 1000 slightly different behaviors.
		// But the IDEA of a full-stack behavior is interesting. By FullStack, I mean a thing that is a Behavior and a Communicator all on its own.
	}

	@Override
	public void executeBehavior(Agent agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessageReceived(Message message) {
		// TODO Auto-generated method stub
		
	}
}
