package org.baseagent.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.baseagent.grid.Grid;
import org.baseagent.sim.Simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GridCanvasContext {
	private GraphicsContext graphicsContext;
	private List<Color> colorPalette;
	private Map<String, Object> properties;
	private Grid grid;
	private GridCanvas gridCanvas;
	private Simulation simulation;
	private int cellWidth, cellHeight;
	private int cellXSpacing, cellYSpacing;
	
	public GridCanvasContext(Simulation simulation, Grid grid, GridCanvas gridCanvas, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
		this.simulation = simulation;
		this.grid = grid;
		this.gridCanvas = gridCanvas;
		this.colorPalette = new ArrayList<>();
		this.properties = new HashMap<>();
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.cellXSpacing = cellXSpacing;
		this.cellYSpacing = cellYSpacing;
	}
	
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	public Grid getGrid() {
		return this.grid;
	}
	
	public GridCanvas getGridCanvas() {
		return this.gridCanvas;
	}
	
	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	public int getCellXSpacing() {
		return cellXSpacing;
	}

	public void setCellXSpacing(int cellXSpacing) {
		this.cellXSpacing = cellXSpacing;
	}

	public int getCellYSpacing() {
		return cellYSpacing;
	}

	public void setCellYSpacing(int cellYSpacing) {
		this.cellYSpacing = cellYSpacing;
	}

	public void setGraphicsContext(GraphicsContext gc) {
		this.graphicsContext = gc;
	}
	
	public GraphicsContext getGraphicsContext() {
		return this.graphicsContext;
	}
	
	public void setColorPalette(List<Color> colors) {
		this.colorPalette = colors;
	}
	
	public void setColorPalette(Color... colors) {
		this.colorPalette = List.of(colors);
	}

	public List<Color> getColorPalette() {
		return this.colorPalette;
	}

	public void setProperties(Map<String, Object> props) {
		this.properties = props;
	}
	
	public Map<String, Object> getProperties() {
		return this.properties;
	}
}
