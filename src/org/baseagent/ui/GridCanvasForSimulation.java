package org.baseagent.ui;

import java.util.List;
import java.util.stream.Collectors;

import org.baseagent.grid.Grid;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationListener;

import javafx.animation.AnimationTimer;

public class GridCanvasForSimulation extends GridCanvas implements SimulationListener {
	private Simulation simulation;
	boolean drawBeacons = true;
	boolean drawAgents = true;
	boolean drawToasts = true;

	public GridCanvasForSimulation(Simulation simulation, Grid grid) {
		this(simulation, grid, 5, 5, 0, 0);
	}
	
	public GridCanvasForSimulation(Simulation simulation, Grid grid, int cellWidth, int cellHeight) {
		this(simulation, grid, cellWidth, cellHeight, 0, 0);
	}
	
	public GridCanvasForSimulation(Simulation simulation, Grid grid, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
		super(grid, cellWidth, cellHeight, cellXSpacing, cellYSpacing);
		this.simulation = simulation;
		this.simulation.addSimulationListener(this);
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				GridCanvasForSimulation.this.update();
			}
		};
		timer.start();
	}
	
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	public boolean drawBeacons() {
		return this.drawBeacons;
	}
	
	public void setDrawBeacons(boolean drawBeacons) {
		this.drawBeacons = drawBeacons;
	}

	public boolean drawAgents() {
		return this.drawAgents;
	}
	
	public void setDrawAgents(boolean drawAgents) {
		this.drawAgents = drawAgents;
	}

	public boolean drawToasts() {
		return this.drawToasts;
	}
	
	public void setDrawToasts(boolean drawToasts) {
		this.drawToasts = drawToasts;
	}

	@Override
	protected void update0(GridCanvasContext gcc) {
		
		// Then draw beacons
		if (drawBeacons) {
			List<Drawable> drawableBeacons = simulation.getBeacons().stream().filter(a -> a instanceof Drawable).map(a -> (Drawable)a).collect(Collectors.toList());
			drawableBeacons.forEach(beacon -> ((Drawable)beacon).drawBefore(gcc));
			drawableBeacons.forEach(beacon -> ((Drawable)beacon).draw(gcc));
			drawableBeacons.forEach(beacon -> ((Drawable)beacon).drawAfter(gcc));
		}

		// Then draw agents
		if (drawAgents) {
			List<Drawable> drawableAgents = simulation.getAgents().stream().filter(a -> a instanceof Drawable).map(a -> (Drawable)a).collect(Collectors.toList());
			drawableAgents.forEach(agent -> ((Drawable)agent).drawBefore(gcc));
			drawableAgents.forEach(agent -> ((Drawable)agent).draw(gcc));
			drawableAgents.forEach(agent -> ((Drawable)agent).drawAfter(gcc));
		}
		
		// Finally, draw toasts on top
		if (drawToasts) {
			// Then draw any toasts, and remove old toasts
			List<Drawable> drawableToasts = getToasts().stream().filter(a -> a instanceof Drawable).map(a -> (Drawable)a).collect(Collectors.toList());
			drawableToasts.stream().filter(toast -> ((Toast)toast).isActive(getSimulation())).forEach(toast -> toast.draw(gcc));
			drawableToasts.removeIf(toast -> ((Toast)toast).readyToRemove(getSimulation()));
		}

	}

	@Override
	protected String generateSnapshotFilename(String filenameBeginning) {
		return filenameBeginning + "_" + String.format("%06d",  getSimulation().getStepTime())+".png";
	}
	
}

