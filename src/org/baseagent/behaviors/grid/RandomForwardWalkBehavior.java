package org.baseagent.behaviors.grid;

import java.util.Random;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.sim.GridAgent;
import org.baseagent.util.CellPoint2D;

public class RandomForwardWalkBehavior implements Behavior {
	private Random random;
	
	public RandomForwardWalkBehavior() {
		super();
		this.random = new Random();
	}
	
	@Override
	public void executeBehavior(Agent xagent) {
		CellPoint2D[] possibleNextPoints = new CellPoint2D[3];
		GridAgent agent = (GridAgent)xagent;
		double heading = agent.getHeading();
		double eighths = ((Math.PI * 2.0) / 8.0) + (Math.PI / 16.0);
		if ((heading > eighths*1) && (heading <= eighths * 2)) {
			possibleNextPoints[0] = new CellPoint2D(agent.getCellX()+1, agent.getCellY());
			possibleNextPoints[1] = new CellPoint2D(agent.getCellX()+1, agent.getCellY()-1);
			possibleNextPoints[2] = new CellPoint2D(agent.getCellX(), agent.getCellY()-1);
		}
		else if ((heading > eighths*2) && (heading <= eighths * 3)) {
			possibleNextPoints[0] = new CellPoint2D(agent.getCellX()+1, agent.getCellY()-1);
			possibleNextPoints[1] = new CellPoint2D(agent.getCellX(), agent.getCellY()-1);
			possibleNextPoints[2] = new CellPoint2D(agent.getCellX()-1, agent.getCellY()-1);
		}
		else if ((heading > eighths*3) && (heading <= eighths * 4)) {
			possibleNextPoints[0] = new CellPoint2D(agent.getCellX(), agent.getCellY()-1);
			possibleNextPoints[1] = new CellPoint2D(agent.getCellX()-1, agent.getCellY()-1);
			possibleNextPoints[2] = new CellPoint2D(agent.getCellX()-1, agent.getCellY());
		}
		else if ((heading > eighths*4) && (heading <= eighths * 5)) {
			possibleNextPoints[0] = new CellPoint2D(agent.getCellX()-1, agent.getCellY()-1);
			possibleNextPoints[1] = new CellPoint2D(agent.getCellX()-1, agent.getCellY());
			possibleNextPoints[2] = new CellPoint2D(agent.getCellX()-1, agent.getCellY()+1);
		}
		else if ((heading > eighths*5) && (heading <= eighths * 6)) {
			possibleNextPoints[0] = new CellPoint2D(agent.getCellX()-1, agent.getCellY());
			possibleNextPoints[1] = new CellPoint2D(agent.getCellX()-1, agent.getCellY()+1);
			possibleNextPoints[2] = new CellPoint2D(agent.getCellX(), agent.getCellY()+1);
		}
		else if ((heading > eighths*6) && (heading <= eighths * 7)) {
			possibleNextPoints[0] = new CellPoint2D(agent.getCellX()-1, agent.getCellY()+1);
			possibleNextPoints[1] = new CellPoint2D(agent.getCellX(), agent.getCellY()+1);
			possibleNextPoints[2] = new CellPoint2D(agent.getCellX()+1, agent.getCellY()+1);
		}
		else if ((heading > eighths*7) && (heading <= eighths * 8)) {
			possibleNextPoints[0] = new CellPoint2D(agent.getCellX(), agent.getCellY()+1);
			possibleNextPoints[1] = new CellPoint2D(agent.getCellX()+1, agent.getCellY()+1);
			possibleNextPoints[2] = new CellPoint2D(agent.getCellX()+1, agent.getCellY());
		}
		else if ((heading > eighths*8) && (heading <= eighths * 1)) {
			possibleNextPoints[0] = new CellPoint2D(agent.getCellX()+1, agent.getCellY()+1);
			possibleNextPoints[1] = new CellPoint2D(agent.getCellX()+1, agent.getCellY());
			possibleNextPoints[2] = new CellPoint2D(agent.getCellX()+1, agent.getCellY()-1);
		}
		
		int index = random.nextInt(3);
		agent.moveTo(possibleNextPoints[index]);
	}
}
