package org.baseagent.grid.rgb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GridLayerAndObject {
	public String gridLayer;
	public Object object;

	public GridLayerAndObject(String gridLayer) {
		this(gridLayer, null);
	}

	public GridLayerAndObject(String gridLayer, Object object) {
		this.gridLayer = gridLayer;
		this.object = object;
	}
	
	/**
	 * Takes a file in the following form, and converts it to a map:
	 * 
	 * (regular text file)
	 * r,b,g:layer
	 * a,r,g,b:layer
	 * r,b,g:layer,object
	 * a,r,b,g:layer,object
	 * # Denotes a comment, either on its own line or at the end of an rgb line
	 * 
	 * @param rgbFile
	 * @return
	 * @throws IOException 
	 */
	public static Map<RGB, GridLayerAndObject> generateFromRGB(File rgbFile) throws IOException {
		Map<RGB, GridLayerAndObject> map = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(rgbFile))) {
			int lineCounter = 1;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.charAt(0) != '#') {
					line = line.replaceAll("\\s","");
					String[] hashSplit = line.split("#");
					String[] colonSplit = hashSplit[0].split(":");
					String[] argbSplit = colonSplit[0].split(",");
					RGB rgb = null;
					try {
						if (argbSplit.length == 3) {
							rgb = new RGB(Integer.parseInt(argbSplit[0].trim()), Integer.parseInt(argbSplit[1].trim()), Integer.parseInt(argbSplit[2].trim()));
						}
						else if (argbSplit.length == 4) {
							rgb = new RGB(Integer.parseInt(argbSplit[0].trim()), Integer.parseInt(argbSplit[1].trim()), Integer.parseInt(argbSplit[2].trim()), Integer.parseInt(argbSplit[3].trim()));
						}
						else {
							throw new IllegalArgumentException("Error reading RGB values in line "+lineCounter+", \""+line+"\"");
						}
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Error reading RGB values in line "+lineCounter+", \""+line+"\"");
					}
					
					if (colonSplit.length < 2) {
						throw new IllegalArgumentException("Error reading GridLayer in line "+lineCounter+", \""+line+"\"");
					}
					String[] gridLayerSplit = colonSplit[1].split(",");
					GridLayerAndObject glo = null;
					if (gridLayerSplit.length == 1) {
						glo = new GridLayerAndObject(gridLayerSplit[0]);
					}
					else if (gridLayerSplit.length == 2) {
						glo = new GridLayerAndObject(gridLayerSplit[0], gridLayerSplit[1]);
					}
					else {
						throw new IllegalArgumentException("Error reading GridLayer and Object values in line "+lineCounter+", \""+line+"\"");
					}

					map.put(rgb, glo);
					lineCounter++;
				}
			}
			reader.close();
		}
		return map;
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o == null) || (!(o instanceof GridLayerAndObject))) {
			return false;
		}
		
		GridLayerAndObject o2 = (GridLayerAndObject)o;
		return (gridLayer.equals(o2.gridLayer)&&(object.equals(o2.object)));
	}
	
	@Override
	public int hashCode() {
		return gridLayer.hashCode()+object.hashCode();
	}
}
