package org.baseagent.ui.defaults;

import java.util.HashMap;
import java.util.Map;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;
import org.baseagent.ui.GridCanvas;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.GridCellRenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class QuickSimulation {
	private Simulation simulation;
	private Grid grid;
	private GridLayer layer;
	private GridCanvas canvas;
	
	public QuickSimulation(int widthInCells, int heightInCells, int cellWidth, int cellHeight) {
		this.simulation = new Simulation();
		this.grid = new Grid(widthInCells, heightInCells);
		this.layer = grid.createGridLayer(GRIDLAYER_DEFAULT, GridLayerUpdateOption.NEXT_BECOMES_CURRENT);

		this.simulation.setUniverse(grid);
		this.simulation.setDelayAfterEachStep(100);
		this.simulation.endWhen(sim -> sim.getStepTime() == 10000);
		
		this.canvas = new GridCanvas(simulation, grid, cellWidth, cellHeight);
		canvas.addGridLayerRenderer(GRIDLAYER_DEFAULT, new GridCellRenderer() {
			private Map<Object, Paint> colorForObject = new HashMap<>();
			private int nextColor = 0;
			private Paint[] colors = new Paint[] { Color.BLACK, Color.RED, Color.WHITE, Color.CYAN, Color.MAGENTA, Color.GREEN, Color.BLUE, Color.YELLOW, 
			                                       Color.ORANGE, Color.BROWN, Color.PINK, Color.DARKGRAY, Color.GRAY, Color.LIGHTGREEN, Color.LIGHTBLUE, Color.LIGHTGRAY  }; 
			
			@Override
			public void drawCell(GridCanvasContext gcc, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
				if (!colorForObject.containsKey(value)) {
					colorForObject.put(value, colors[nextColor]);
					nextColor = (nextColor+1) % colors.length;
				}

				GraphicsContext gc = (GraphicsContext)gcc.getGraphicsContext();
				gc.setFill(colorForObject.get(value));
				gc.fillRect(xInPixels+1, yInPixels+1, widthInPixels-2, heightInPixels-2);
			}
		});
	}

	public Simulation getSimulation() {
		return simulation;
	}

	public Grid getGrid() {
		return grid;
	}

	public GridLayer getDefaultGridLayer() {
		return layer;
	}
	
	public GridCanvas getGridCanvas() {
		return canvas;
	}
	
	//
	// PASS-THROUGHS
	//
	
	public void add(SimulationComponent simulatee) {
		getSimulation().add(simulatee);
	}
	
	public void fill(Object value) {
		getDefaultGridLayer().fill(value);
	}
	
	public void fill(Object value, int x1, int y1, int x2, int y2) {
		getDefaultGridLayer().fill(value, x1, y1, x2, y2);
	}
	
	public void scatter(Object value, int count) {
		getDefaultGridLayer().scatter(value, count);
	}
	
	public void scatter(Object value, int count, int x1, int y1, int x2, int y2) {
		getDefaultGridLayer().scatter(value, count, x1, y1, x2, y2);
	}
	
	public void form(Object value, int x, int y, String... strings) {
		getDefaultGridLayer().form(value, x, y, strings);
	}
	
	public void start() {
		getSimulation().start();
	}
	
	public static final String GRIDLAYER_DEFAULT = "GRIDLAYER_DEFAULT";
}
