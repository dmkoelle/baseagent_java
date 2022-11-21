package org.baseagent.grid.textmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.baseagent.Environment;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridCell;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.grid.GridPosition;
import org.baseagent.network.Network;
import org.baseagent.network.Node;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.GridCanvasForSimulation;
import org.baseagent.ui.GridCellRenderer;
import org.baseagent.util.Pair;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The TextMapProcessor lets you specify layers of grid maps through a text file. You declare legends for those maps, and this TextMapProcessor
 * also lets you specify transitions from one state to another for elements on the grids.
 * 
 * Create a text file with the following specifications:
 * 
 * - Lines starting with a hash symbol are always considered a comment
 * - After some initial comments if desired, make sure the file starts by specifying the width and height of the map:
 *   Width = 20
 *   Height = 20
 * - Now you're ready to draw a map layer. Start by giving a name to the layer, like so:
 *   Layer: GroundLayer
 * - The name of the layer is the same string that you can use in grid.getLayer(String name)
 * - Now make a pattern of width * height symbols. You can use spaces between the symbols and the spaces will be ignored,
 *   which may help with visibility of your map.
 * - Each thing in the layer gets one unique character
 * - At the end, list your legend. Start a line that says Legend. On the next lines, 
 *   for each character that you used in your map, list the letter, followed by a colon, followed by a string representation of that thing's value.
 *   . : empty
 *   B : box
 *   T : tree
 * - You can then define additional layers
 * - You can define a layer of double values. Just use space-separated double values instead of text symbols. There is no legend for a doubles layer.
 * - Conditionals: In your legend, you can list a set of possible values for your symbol based on the status of the same position in other layers:
 *   + : GroundLayer.water ? raft; GroundLayer.dirt, PheromoneLayer.red ? food
 * - Immediately after any object value that is meant to be drawn to the screen, you can add a space, a hash, and an RGB value to tell what color the thing should be:
 *   T : SeasonLayer.summer ? leafy-tree #005555; SeasonLayer.autumn ? fall-tree #774400; SeasonLayer.winter ? bare-tree #666666; SeasonLayer.spring ? blossom-tree #997777
 * - Layers will be drawn to the screen in the order they are presented in the file
 * 
 * This file also contains the ability to create special objects from maps, such as a waypoint network.
 */
public class TextMapProcessor {
	private int width, height;
	List<String> layerNames;
	Map<String, String[][]> layerToMapStringsFromFile;
	Map<String, List<String>> layerToLegendStringsFromFile;
	Map<String, Map<Character, LegendLine>> layerToProcessedLegend;
	Map<String, Map<String, Color>> layerToValueToColor;
	
	class LegendLine {
		String ch;
		List<Condition> conditions = new ArrayList<>();
		String defaultValue;
		
		public Object getValue(Grid grid, int x, int y, Object currentValue) {
			Object retVal = null;
			for (Condition condition : conditions) {
				retVal = condition.test(grid, x, y, currentValue);
				if ((!(retVal == null)) && !retVal.equals(currentValue)) return retVal;
			}
			return currentValue;
		}
	}
	
	class Condition { 
		List<Pair<String, String>> antecedentLayerAndValue = new ArrayList<>();
		Object consequentValue;
		
		public Object test(Grid grid, int x, int y, Object currentValue) {
			if (antecedentLayerAndValue.isEmpty()) {
				return currentValue == null ? consequentValue : currentValue;
			}
			
			for (Pair<String, String> layerAndValue : antecedentLayerAndValue) {
				if (grid.getGridLayer(layerAndValue.getFirst()) == null) {
					return currentValue;
				}
				if ((grid.getGridLayer(layerAndValue.getFirst()).get(x, y) != null) && !grid.getGridLayer(layerAndValue.getFirst()).get(x, y).equals(layerAndValue.getSecond())) {
					return currentValue;
				}
			}
			
			return consequentValue;
		}
	}
	
	public TextMapProcessor(File file) throws IOException {
		this.layerNames = new ArrayList<>();
		this.layerToMapStringsFromFile = new HashMap<>();
		this.layerToLegendStringsFromFile = new HashMap<>();
		this.layerToProcessedLegend = new HashMap<>();
		this.layerToValueToColor = new HashMap<>();
		
		parseFile(file);
		processLegend();
	}
	
	private void parseFile(File file) throws IOException {
		int NO_MODE = -1, READING_MAP = 0, READING_LEGEND = 1;
		
		// Read the file
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		String layerName = null;
		
		int mode = NO_MODE;
		int lineCounter = 0;
		int rowCounter = 0;
		int columnCounter = 0;
		String[][] rows = null;
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith(COMMENT) && !line.trim().equals("")) {
				columnCounter = 0;
				
				if (line.toUpperCase().startsWith(LAYER)) {
					mode = READING_MAP;
					layerName = line.substring(line.indexOf(':')+1, line.length()).trim();
					layerNames.add(layerName);
					
					rows = new String[this.height][];
					for (int i=0; i < this.height; i++) {
						rows[i] = new String[this.width];
					}
					
					layerToMapStringsFromFile.put(layerName, rows);
					layerToLegendStringsFromFile.put(layerName, new ArrayList<String>());
					rowCounter = 0;
				}
				else if (line.toUpperCase().startsWith(LEGEND)) {
					mode = READING_LEGEND;
				}
				else if (line.toUpperCase().startsWith(WIDTH)) {
					String widthString = line.substring(line.indexOf('=')+1, line.length()).trim();
					this.width = Integer.parseInt(widthString);
				}
				else if (line.toUpperCase().startsWith(HEIGHT)) {
					String heightString = line.substring(line.indexOf('=')+1, line.length()).trim();
					this.height = Integer.parseInt(heightString);
				}
				else {
					if ((layerName == null) || (layerName.equals(""))) {
						reader.close();
						throw new RuntimeException("Error at Line "+lineCounter+": No layer name");
					}
					if (mode == READING_MAP) {
						try {
							Double d = Double.parseDouble(line.split(" ")[0]);
							for (String s : line.split(" ")) {
								if (!s.trim().equals("")) {
									rows[rowCounter][columnCounter] = s;
									columnCounter++;
								}
							}
							rowCounter++;
							columnCounter = 0;
						} catch (NumberFormatException e) {
							for (Character ch : line.toCharArray()) {
								if (ch != ' ') {
									rows[rowCounter][columnCounter] = Character.toString(ch);
									columnCounter++;
									if (columnCounter > width) {
										throw new RuntimeException("Is line "+lineCounter+" too long? Looks like it exceeds width");
									}
								}
							}
							rowCounter++;
							columnCounter = 0;
						}
					}
					else if (mode == READING_LEGEND) {
						layerToLegendStringsFromFile.get(layerName).add(line);
					}
				}
			}
			lineCounter++;
		}
		reader.close();
	}
	
	private void processLegend() {
		for (Map.Entry<String, List<String>> layerToLegend : layerToLegendStringsFromFile.entrySet()) {
			String layerName = layerToLegend.getKey();
			Map<Character, LegendLine> charToLegendLine = new HashMap<>();
			Map<String, Color> valueToColor = new HashMap<>();
			layerToProcessedLegend.put(layerName, charToLegendLine);
			layerToValueToColor.put(layerName, valueToColor);
			for (String legendLineString : layerToLegend.getValue()) {
				// A LegendLine looks like this:
				//
				// . : empty
				// B : GroundLayer.water ? raft #889900; GroundLayer.dirt ? wood #554433
				// T : GroundLayer.dirt, SeasonLayer.spring ? blooming-tree #777766
				//
				// CHAR COLON [CONDITION] VALUE [COLOR] [SEMICOLON] <another Condition?-Value-Color? etc.>
				
				LegendLine legendLine = new LegendLine();
				char ch = legendLineString.substring(0, legendLineString.indexOf(":")).trim().charAt(0);
				String[] cdrs = legendLineString.substring(legendLineString.indexOf(":")+1, legendLineString.length()).split(";");
				for (String cdr : cdrs) {
					int indexOfValue = 0;
					Condition condition = new Condition();
					if (cdr.indexOf("?") > -1) {
						indexOfValue = cdr.indexOf("?")+1;
						// There's a condition involved
						String condString = cdr.substring(0, cdr.indexOf("?")).trim();
						String[] conds = condString.split(",");
						for (String cond : conds) {
							String condLayer = cond.substring(0, cond.indexOf(".")).trim();
							String condValue = cond.substring(cond.indexOf(".")+1, cond.length()).trim();
							Pair<String, String> condPair = new Pair<>(condLayer, condValue);
							condition.antecedentLayerAndValue.add(condPair);
						}
					}
					int indexOfColor = cdr.indexOf("#");
					int endOfValue = cdr.length();
					if (indexOfColor > -1) endOfValue = indexOfColor;
					
					String val = cdr.substring(indexOfValue, endOfValue).trim();
					condition.consequentValue = val;
					
					if (indexOfColor > -1) {
						String colorString = cdr.substring(indexOfColor, cdr.length()).trim();
						valueToColor.put(val, Color.valueOf(colorString));
					}
					legendLine.conditions.add(condition);
				}
				charToLegendLine.put(ch, legendLine);
			}
		}
	}

	public Grid generateGrid() {
		if (this.width == 0) throw new RuntimeException("Error: No width defined in map file. Use WIDTH = x");
		if (this.height == 0) throw new RuntimeException("Error: No height defined in map file. Use HEIGHT = y");
		
		Grid grid = new Grid(width, height);
		for (String layerName : layerNames) {
			GridLayer layer = grid.createGridLayer(layerName, GridLayerUpdateOption.NO_SWITCH);
			populateGridLayer(layer, width, height, layerToMapStringsFromFile.get(layerName), layerToProcessedLegend.get(layerName));
		}
		return grid;
	}
	
	private void populateGridLayer(GridLayer layer, int width, int height, String[][] mapStrings, Map<Character, LegendLine> legend) {
		if ((mapStrings.length > 0) && (mapStrings[0][0] != null)) {
			// If the values seem to be numeric, populate the grid layer numerically
			try {
				Double.parseDouble(mapStrings[0][0]);
				
				// If we got this far, parse the grid layer as numeric
				populateGridLayer_Double(layer, width, height, mapStrings);
				return;
			} catch (NumberFormatException e) {
				populateGridLayer_Legend(layer, width, height, mapStrings, legend);
			}
		} else {
			throw new RuntimeException("Error with map strings, see TextMapProcessor.populateGridLayer");
		}
	}
	
	private void populateGridLayer_Double(GridLayer layer, int width, int height, String[][] mapStrings) {
		for (int i=0; i < height; i++) {
			for (int u=0; u < width; u++) {
				Double d = Double.parseDouble(mapStrings[i][u]);
				layer.set(u, i, d);
			}
		}
	}
	
	private void populateGridLayer_Legend(GridLayer layer, int width, int height, String[][] mapStrings, Map<Character, LegendLine> legend) {
		for (int i=0; i < height; i++) {
			for (int u=0; u < width; u++) {
				LegendLine legendLine = legend.get(mapStrings[i][u].charAt(0));
				layer.set(u, i, legendLine.getValue(layer.getParentGrid(), i, u, layer.get(u, i)));
			}
		}
	}
	
	public GridCanvas generateGridCanvas(Simulation sim, boolean isSimulation, Grid grid, int cellWidth, int cellHeight) throws IOException {
		GridCanvas gridCanvas = isSimulation ? new GridCanvasForSimulation(sim, grid, cellWidth, cellHeight) : new GridCanvas(grid, cellWidth, cellHeight);
		for (String layerName : layerNames) {
			gridCanvas.addGridLayerRenderer(layerName, new GridCellRenderer() {
				@Override
				public void drawCell(GridCanvasContext gcc, GridLayer layer, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
					if (value instanceof Double) {
						drawCellDouble(gcc, value, xInPixels, yInPixels, widthInPixels, heightInPixels);
					} else {
						drawCellString(gcc, layer, value, xInPixels, yInPixels, widthInPixels, heightInPixels);
					}
				}
				
				private void drawCellDouble(GridCanvasContext gcc, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
					Color color = new Color(1.0 * (double)value, 1.0 * (double)value, 0.0, 1.0);
					GraphicsContext gc = gcc.getGraphicsContext();
					gc.setFill(color);
					gc.fillRect(xInPixels + 2, yInPixels + 2, widthInPixels - 3, heightInPixels - 3);
				}

				private void drawCellString(GridCanvasContext gcc, GridLayer layer, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
					if (!layerToValueToColor.containsKey(layer.getLayerName())) {
						return;
					}
					Map<String, Color> valueToColor = layerToValueToColor.get(layer.getLayerName());
					if (!valueToColor.containsKey(value)) {
						return; // Not having a color defined for a value in a cell will mean that the cell won't be painted - this is intentional
					}
					Color color = valueToColor.get(value);
					GraphicsContext gc = gcc.getGraphicsContext();
					gc.setFill(color);
					gc.fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
					gc.setStroke(color.darker());
					gc.setLineWidth(0.25);
					gc.strokeRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
				}
			});
		}
		return gridCanvas;
	}
	
	/**
	 * Depends on there being a property in the SImulation called SIM_PROPERTY_ALL_GRIDS
	 */
	public Environment generateEnvironment() throws IOException {
		Environment env = new Environment() {
			@Override
			public void step(Simulation sim) {
				List<Grid> grids = (List<Grid>)sim.getProperties().get(TextMapProcessor.SIM_PROPERTY_ALL_GRIDS);
				for (Grid grid : grids) {
					updateGrid(grid);
				}
			}
		};
		return env;
	}
	
	public void updateGrid(Grid grid) {
		for (String layerName : layerNames) {
			for (GridCell cell : grid.getGridLayer(layerName)) {
				if ((cell.getCellY() >= height) || (cell.getCellX() >= width)) {
					// Something's up
				} else {
					String v = layerToMapStringsFromFile.get(layerName)[cell.getCellY()][cell.getCellX()];
					if (layerToProcessedLegend.containsKey(layerName)) {
						LegendLine line = layerToProcessedLegend.get(layerName).get(v.charAt(0));
						if (line != null) {
							Object currentValue = cell.get();
							Object newValue = line.getValue(grid, cell.getCellX(), cell.getCellY(), currentValue);
							if ((newValue != null) && (!newValue.equals(currentValue))) {
								cell.set(newValue);
							}
						}
					}
				}
			}
		}
	}
	
	public Network<GridPosition, Double> makeWaypointNetwork(Grid groundTruthGrid, GridLayer waypointLayer, Predicate<GridPosition> barrierCondition) {
		Network<GridPosition, Double> network = new Network<>();
		
		// First, find all of the waypoints and the root
		List<GridPosition> positionOfWaypoints = new ArrayList<>();
		GridPosition positionOfRoot = null;
		for (GridCell cell : waypointLayer) {
			if (cell.get().equals(WAYPOINT) || cell.get().equals(ROOT)) {
				positionOfWaypoints.add(cell.getGridPosition());
			} 
			if (cell.get().equals(ROOT)) {
				positionOfRoot = cell.getGridPosition();
			}
		}
		
		for (GridPosition waypoint : positionOfWaypoints) {
			Node<GridPosition> node = new Node<>(waypoint);
			network.addNode(node);
			if (waypoint == positionOfRoot) {
				network.setRoot(node);
			}
		}
		
		// Next, for each waypoint that can see another waypoint, link it
		network.connectVisibleNodes(groundTruthGrid, barrierCondition);
		
		return network;
	}
	
	// Keys used in the text files
	public static final String COMMENT = "#";
	public static final String LAYER = "LAYER";
	public static final String LEGEND = "LEGEND";
	public static final String WIDTH = "WIDTH";
	public static final String HEIGHT = "HEIGHT";

	public static final String SIM_PROPERTY_ALL_GRIDS = "SIM_PROPERTY_ALL_GRIDS";
	public static final String WAYPOINT = "WAYPOINT";
	public static final String ROOT = "ROOT";
}