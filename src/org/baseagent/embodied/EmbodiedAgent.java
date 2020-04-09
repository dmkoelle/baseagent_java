package org.baseagent.embodied;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.behaviors.Behavior;
import org.baseagent.embodied.effectors.EmbodiedEffector;
import org.baseagent.embodied.sensors.EmbodiedSensor;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.network.Network;
import org.baseagent.sim.GridAgent;
import org.baseagent.ui.Drawable;
import org.baseagent.ui.SimulationCanvasContext;
import org.baseagent.ui.defaults.VisualizationLibrary;
import org.baseagent.util.Vector2D;

import javafx.scene.paint.Color;

public class EmbodiedAgent extends GridAgent implements HasBody {
	private Behavior embodiedBehavior;
	private Grid bodyGrid;
	private Network bodyNetwork;
	private List<EmbodiedSensor> sensors;
	private List<Processor> processors;
	private List<EmbodiedEffector> effectors;
	private List<Vector2D> forcesToApply;
	private EmbodiedAgent primeAgent; // In cases of joining other agents
	
	public EmbodiedAgent(int bodyWidth, int bodyHeight) {
		super();
		initAgent(bodyWidth, bodyHeight);
	}
	
	public EmbodiedAgent(String gridLayerName, int bodyWidth, int bodyHeight) {
		super(gridLayerName);
		initAgent(bodyWidth, bodyHeight);
	}
	
	private void initAgent(int bodyWidth, int bodyHeight) {
		this.bodyGrid = new Grid(bodyWidth, bodyHeight);
		this.bodyGrid.createGridLayer("body");
//		this.bodyLogic = new NetworkBehavior<>();
		this.embodiedBehavior = new EmbodiedBehavior();
		sensors = new ArrayList<>();
		processors = new ArrayList<>();
		effectors = new ArrayList<>();
		forcesToApply = new ArrayList<>();
		setBehaviorPolicy(embodiedBehavior);
		
		this.setDrawable(new Drawable() {
			@Override
			public void draw(SimulationCanvasContext sc) {
				// TODO: actually, you do want this agent to draw itself if it's asked to by the prime agent
				if ((EmbodiedAgent.this.primeAgent != null) && (EmbodiedAgent.this.primeAgent != EmbodiedAgent.this)) {
					VisualizationLibrary.drawEmbodiedAgent(sc.getGraphicsContext(), getCellX(), getCellY(), sc.getCellWidth(), sc.getCellHeight(), EmbodiedAgent.this, getColorOrUse(Color.LIGHTBLUE));
				} else {
					VisualizationLibrary.drawEmbodiedAgent(sc.getGraphicsContext(), getCellX(), getCellY(), sc.getCellWidth(), sc.getCellHeight(), EmbodiedAgent.this, getColorOrUse(Color.LIGHTBLUE));
				}
			}
		});
	}
	
	// 
	// Join subsystem
	//
	
	public void join(EmbodiedAgent otherAgent) {
		if (otherAgent.primeAgent == null) {
			otherAgent.primeAgent = otherAgent;
		}
		this.primeAgent = otherAgent.primeAgent;
	}
	
	public void disjoin() {
		this.primeAgent = null;
	}
	
	//
	// Force subsystem
	//
	
	public void addForce(Vector2D force) {
		if (this.primeAgent == null) {
			this.forcesToApply.add(force);
		} else {
			this.primeAgent.addForce(force);
		}
	}
	
	public void processForces() {
		Vector2D vector = new Vector2D(0, 0);
		for (Vector2D force : forcesToApply) {
			vector.add(force);
		}
		this.moveAlong(vector);
	}
	
	//
	// Body subsystem
	//

	@Override
	public Grid getBody() {
		return this.bodyGrid;
	}

	@Override
	public Behavior getBodyLogic() {
		return this.embodiedBehavior;
	}

	@Override
	public GridLayer getParentGridLayer() {
		return super.getGridLayer();
	}
	
	public void place(int x, int y, HasGridPosition spe) {
		getBody().getGridLayer("body").current().set(x, y, spe);
		spe.setCellX(x);
		spe.setCellY(y);
		
		if (spe instanceof EmbodiedSensor) {
			sensors.add((EmbodiedSensor)spe);
		} else if (spe instanceof EmbodiedEffector) {
			effectors.add((EmbodiedEffector)spe);
		} else if (spe instanceof Processor) {
			processors.add((Processor)spe);
		}
	}
	
	public void incorporate(HasGridPosition spe) {
		place(spe.getCellX(), spe.getCellY(), spe);
	}
	
	public void incorporate(SBEPackage spes) {
		for (EmbodiedSensor sensor : spes.getSensors()) {
			incorporate(sensor);
		}
		for (EmbodiedBehavior behavior : spes.getBehaviors()) {
			incorporate(behavior);
		}
		for (EmbodiedEffector effector : spes.getEffectors()) {
			incorporate(effector);
		}
	}
	
	public void disincorporate(HasGridPosition spe) {
		System.out.println("TODO: EmbodiedAgent.disincorporate"); // TODO: EmbodiedAgent.disincorporate
	}
	
	public void remove(HasGridPosition spe) {
		getBody().getGridLayer("body").current().clear(spe.getCellX(), spe.getCellY());
		
		if (spe instanceof EmbodiedSensor) {
			sensors.remove((EmbodiedSensor)spe);
		} else if (spe instanceof EmbodiedEffector) {
			effectors.remove((EmbodiedEffector)spe);
		} else if (spe instanceof Processor) {
			processors.remove((Processor)spe);
		}
	}

	public List<EmbodiedSensor> getSensors() {
		return sensors;
	}

	public List<Processor> getProcessors() {
		return processors;
	}

	public List<EmbodiedEffector> getEffectors() {
		return effectors;
	}

}
