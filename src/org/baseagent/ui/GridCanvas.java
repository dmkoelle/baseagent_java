package org.baseagent.ui;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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
	private Grid grid;
	private String id = DEFAULT_GRID_CANVAS_ID;
	private GridCanvasContext gcc;
	private List<String> orderedListOfLayerNames;
	private Map<String, GridLayerRenderer> renderersByName;
	private List<String> renderersToShow;
	private List<Toast> toasts;
	boolean drawBeacons = true;
	boolean drawAgents = true;
	boolean drawToasts = true;
	
	public GridCanvas(Simulation simulation, Grid grid) {
		this(simulation, grid, 5, 5, 0, 0);
	}

	public GridCanvas(Simulation simulation, Grid grid, int cellWidth, int cellHeight) {
		this(simulation, grid, cellWidth, cellHeight, 0, 0);
	}

	public GridCanvas(Simulation simulation, Grid grid, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
		super(grid.getWidthInCells() * cellWidth + (grid.getWidthInCells()-1) * cellXSpacing, 
			  grid.getHeightInCells() * cellHeight + (grid.getHeightInCells()-1) * cellYSpacing);
		this.simulation = simulation;
		this.simulation.addSimulationListener(this);
		this.grid = grid;
		this.gcc = new GridCanvasContext(simulation, grid, this, cellWidth, cellHeight, cellXSpacing, cellYSpacing);
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

	public GridCanvas(String id, Simulation simulation, Grid grid) {
		this(id, simulation, grid, 5, 5, 0, 0);
	}

	public GridCanvas(String id, Simulation simulation, Grid grid, int cellWidth, int cellHeight) {
		this(id, simulation, grid, cellWidth, cellHeight, 0, 0);
	}

	public GridCanvas(String id, Simulation simulation, Grid grid, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
		this(simulation, grid, cellWidth, cellHeight, cellXSpacing, cellYSpacing);
		this.id = id;
	}

	public String getGridCanvasId() {
		return this.id;
	}
	
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	public Grid getGrid() {
		return this.grid;
	}
	
	public GridCanvasContext getGridCanvasContext() {
		return this.gcc;
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
	
	public boolean drawsBeacons() {
		return drawBeacons;
	}

	public void setDrawBeacons(boolean drawBeacons) {
		this.drawBeacons = drawBeacons;
	}

	public boolean drawsAgents() {
		return drawAgents;
	}

	public void setDrawAgents(boolean drawAgents) {
		this.drawAgents = drawAgents;
	}

	public boolean drawsToasts() {
		return drawToasts;
	}

	public void setDrawToasts(boolean drawToasts) {
		this.drawToasts = drawToasts;
	}

	public List<Toast> getToasts() {
		return this.toasts;
	}
	
	public void update() {
		GraphicsContext gc = this.getGraphicsContext2D();
		gcc.setGraphicsContext(gc);
		
		// Clear everything
		Color backgroundColor = Color.WHITE;
		if (gcc.getColorPalette().size() > 0) backgroundColor = gcc.getColorPalette().get(0);
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// First, draw the grid layers
		for (String layerName : orderedListOfLayerNames) {
			GridLayerRenderer renderer = renderersByName.get(layerName);
			if ((renderer != null) && (grid.getGridLayer(layerName) != null)) {
				renderer.draw(gcc, grid.getGridLayer(layerName), this.getWidth(), this.getHeight());
			}
		}
		
		if (drawBeacons) {
			// Then draw beacons
			simulation.getBeacons().stream().forEach(beacon -> ((Drawable)beacon).drawBefore(gcc));
			simulation.getBeacons().stream().forEach(beacon -> ((Drawable)beacon).draw(gcc));
			simulation.getBeacons().stream().forEach(beacon -> ((Drawable)beacon).drawAfter(gcc));
		}
		
		if (drawAgents) {
			// Then draw agents
			simulation.getAgents().stream().forEach(agent -> ((Drawable)agent).drawBefore(gcc));
			simulation.getAgents().stream().forEach(agent -> ((Drawable)agent).draw(gcc));
			simulation.getAgents().stream().forEach(agent -> ((Drawable)agent).drawAfter(gcc));
		}
		
		if (drawToasts) {
			// Then draw any toasts, and remove old toasts
			toasts.stream().filter(toast -> toast.isActive(getSimulation())).forEach(toast -> toast.draw(gcc));
			toasts.removeIf(toast -> toast.readyToRemove(getSimulation()));
		}
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
	
	public static final String DEFAULT_GRID_CANVAS_ID = "DEFAULT_GRID_CANVAS_ID";
}
