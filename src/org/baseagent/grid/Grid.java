package org.baseagent.grid;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.grid.rgb.GridLayerAndObject;
import org.baseagent.grid.rgb.RGB;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;
import org.baseagent.sim.Universe;

public class Grid extends SimulationComponent implements Universe {
	public static String DEFAULT_GRID_LAYER = "DEFAULT_GRID_LAYER";
	private int widthInCells, heightInCells;
	private Map<String, GridLayer> layers;
	private GridBoundsPolicy boundsPolicy;
	private GridStepPolicy stepPolicy;
	
	public Grid(int widthInCells, int heightInCells) {
		super();
		
		this.widthInCells = widthInCells;
		this.heightInCells = heightInCells;
	
		this.layers = new HashMap<>();
		this.boundsPolicy = new TorusBoundsPolicy(widthInCells, heightInCells);
		this.stepPolicy = new FullGridStepPolicy(this);
		createGridLayer(DEFAULT_GRID_LAYER, GridLayerUpdateOption.NO_SWITCH);  
	}
	
	public static Grid generateFromImage(File imageFile, File rgbMapFile) throws IOException {
		 Map<RGB, GridLayerAndObject> rgbMap = GridLayerAndObject.generateFromRGB(rgbMapFile);
		 return generateFromImage(imageFile, rgbMap);
	}
	
	public static Grid generateFromImage(File imageFile, Map<RGB, GridLayerAndObject> rgbMap) throws IOException {
		 BufferedImage image = ImageIO.read(imageFile);
		 Grid grid = new Grid(image.getWidth(), image.getHeight());
		 for (int x=0; x < image.getWidth(); x++) {
			 for (int y=0; y < image.getHeight(); y++) {
				 RGB rgb = RGB.get(image.getRGB(x, y), ColorModel.getRGBdefault());
				 GridLayerAndObject glo = rgbMap.get(rgb);
				 if (glo != null) {
					 GridLayer layer = grid.getOrCreateGridLayer(glo.gridLayer, GridLayerUpdateOption.NO_SWITCH);
					 if (glo.object != null) {
						 layer.set(x, y, glo.object);
					 }
				 }
			 }
		 }
		 return grid;
	}
	
	public int getWidthInCells() {
		return this.widthInCells;
	}
	
	public int getHeightInCells() {
		return this.heightInCells;
	}
	
	public void setBoundsPolicy(GridBoundsPolicy boundsPolicy) {
		this.boundsPolicy = boundsPolicy;
	}

	public GridBoundsPolicy getBoundsPolicy() {
		return this.boundsPolicy;
	}
	
	public void setStepPolicy(GridStepPolicy stepPolicy) {
		this.stepPolicy = stepPolicy;
	}

	public GridStepPolicy getStepPolicy() {
		return this.stepPolicy;
	}
	
	public GridLayer createGridLayer(String name, GridLayerUpdateOption updateOption) {
		GridLayer layer = new GridLayer(name, this, updateOption);
		this.addGridLayer(name, layer);
		return layer;
	}
	
	public void addGridLayer(String name, GridLayer layer) {
		this.layers.put(name, layer);
	}
	
	public GridLayer getGridLayer(String name) {
		return layers.get(name);
	}
	
	public GridLayer getOrCreateGridLayer(String name, GridLayerUpdateOption updateOption) {
		GridLayer retVal = layers.get(name);
		if (retVal == null) {
			return createGridLayer(name, updateOption);
		} else {
			return retVal;
		}
	}
	
	public void removeGridLayer(String name) {
		this.layers.remove(name);
	}
	
	//
	// Operations on the default layer
	//

	// TODO: Operations on the default layer
	
	public void step() {
		stepPolicy.step();
	}

	public Collection<GridLayer> getGridLayers() {
		return layers.values();
	}
	
	public void swap() {
		for (GridLayer layer : getGridLayers()) {
			layer.switchToNextStep();
		}
	}
	
	@Override
	public Type getType() {
		return SimulationComponent.Type.GRID;
	}

	@Override
	public void onAfterStepStarted(Simulation simulation) {
		step();
	}

	@Override
	public void onBeforeStepEnded(Simulation simulation) {
		swap();
	}
}
