package org.baseagent.ui;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.baseagent.Agent;
import org.baseagent.Beacon;
import org.baseagent.grid.Grid;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationListener;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GridCanvas extends Canvas implements SimulationListener {
	private Simulation simulation;
	private SimulationCanvasContext sc;
	private List<String> orderedListOfLayerNames;
	private Map<String, GridLayerRenderer> renderersByName;
	private List<Toast> toasts;
	
	public GridCanvas(Simulation simulation) {
		this(simulation, 5, 5, 0, 0);
	}

	public GridCanvas(Simulation simulation, int cellWidth, int cellHeight) {
		this(simulation, cellWidth, cellHeight, 0, 0);
	}

	public GridCanvas(Simulation simulation, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
		super(((Grid)simulation.getUniverse()).getWidthInCells() * cellWidth + (((Grid)simulation.getUniverse()).getWidthInCells()-1) * cellXSpacing, 
			  ((Grid)simulation.getUniverse()).getHeightInCells() * cellHeight + (((Grid)simulation.getUniverse()).getHeightInCells()-1) * cellYSpacing);
		this.simulation = simulation;
		this.simulation.addSimulationListener(this);
		this.sc = new SimulationCanvasContext(simulation, cellWidth, cellHeight, cellXSpacing, cellYSpacing);
		this.orderedListOfLayerNames = new ArrayList<>();
		this.renderersByName = new HashMap<>();
		this.toasts = new ArrayList<>();
		
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            	GridCanvas.this.update();
            }
        };
		timer.start();
	}
	
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	public SimulationCanvasContext getSimulationCanvasContext() {
		return this.sc;
	}
	
	public void addGridLayerRenderer(String layerName, GridLayerRenderer r) {
		orderedListOfLayerNames.add(layerName);
		renderersByName.put(layerName, r);
	}

	public void removeGridLayerRenderer(String layerName, GridLayerRenderer r) {
		orderedListOfLayerNames.remove(layerName);
		renderersByName.remove(layerName);
	}
	
	public void addToast(Toast toast) {
		this.toasts.add(toast);
	}
	
	public void removeToast(Toast toast) {
		this.toasts.remove(toast);
	}
	
	public List<Toast> getToasts() {
		return this.toasts;
	}
	
	public void update() {
		GraphicsContext gc = this.getGraphicsContext2D();
		sc.setGraphicsContext(gc);
		
		// Clear everything
		Color backgroundColor = Color.WHITE;
		if (sc.getColorPalette().size() > 0) backgroundColor = sc.getColorPalette().get(0);
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// First, draw the grid layers
		for (String layerName : orderedListOfLayerNames) {
			GridLayerRenderer renderer = renderersByName.get(layerName);
			if ((renderer != null) && (((Grid)simulation.getUniverse()).getGridLayer(layerName) != null)) {
//				System.out.println("SIMULATION CANVAS NEEDS TO UPDATE ITS DRAWING OF THE GRID 2019-09-17"); // TODO: SIMULATION CANVAS NEEDS TO UPDATE ITS DRAWING OF THE GRID 2019-09-17
				renderer.draw(sc, ((Grid)simulation.getUniverse()).getGridLayer(layerName), this.getWidth(), this.getHeight());
			}
		}
		
		// Then draw any agent layers
		for (Agent agent : simulation.getAgents()) {
			if (agent instanceof Drawable) {
				((Drawable)agent).draw(sc);
//				((Drawable)agent).draw(sc, agent.getCellX()*(sc.getCellWidth() + sc.getCellXSpacing()), agent.getCellY()*(sc.getCellHeight() + sc.getCellYSpacing()), sc.getCellWidth(), sc.getCellHeight());
			}
		}

		for (Beacon beacon : simulation.getBeacons()) {
			if (beacon instanceof Drawable) {
				((Drawable)beacon).draw(sc);
//				((Drawable)beacon).draw(sc, beacon.getCellX()*(sc.getCellWidth() + sc.getCellXSpacing()), beacon.getCellY()*(sc.getCellHeight() + sc.getCellYSpacing()), sc.getCellWidth(), sc.getCellHeight());
			}
		}

		// Then draw any toasts, and remove old toasts
		toasts.stream().filter(toast -> toast.isActive(getSimulation())).forEach(toast -> toast.draw(sc));
		toasts.removeIf(toast -> toast.readyToRemove(getSimulation()));
	}
	
	public void saveSnapshot(String filenameBeginning) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				File file = new File(generateSnapshotFilename(filenameBeginning));
				WritableImage writableImage = new WritableImage((int)getWidth(), (int)getHeight());
				GridCanvas.this.snapshot(null, writableImage);
				RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
				try {
					ImageIO.write(renderedImage, "png", file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private String generateSnapshotFilename(String filenameBeginning) {
		return filenameBeginning + "_" + String.format("%06d", getSimulation().getStepTime())+".png";
	}
}
