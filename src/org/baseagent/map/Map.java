package org.baseagent.map;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;
import org.baseagent.sim.Universe;

public class Map extends SimulationComponent implements Universe {
    private int widthInTiles;
    private int heightInTiles;
    private java.util.Map<String, MapLayer> layers;

    public Map(int widthInTiles, int heightInTiles) {
        super();
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.layers = new HashMap<>();
        // create a default layer
        createMapLayer(MapLayer.DEFAULT_MAP_LAYER);
    }

    public int getWidthInTiles() { return this.widthInTiles; }
    public int getHeightInTiles() { return this.heightInTiles; }

    public MapLayer createMapLayer(String name) {
        MapLayer layer = new MapLayer(name, this);
        addMapLayer(name, layer);
        return layer;
    }

    public void addMapLayer(String name, MapLayer layer) {
        this.layers.put(name, layer);
    }

    public MapLayer getMapLayer(String name) {
        return layers.get(name);
    }

    public void removeMapLayer(String name) {
        this.layers.remove(name);
    }

    public Collection<MapLayer> getMapLayers() { return layers.values(); }

    @Override
    public Type getType() {
        return SimulationComponent.Type.GRID; // reuse GRID type for now
    }

    @Override
    public void onAfterStepStarted(Simulation simulation) {
        // noop for basic map
    }

    @Override
    public void onBeforeStepEnded(Simulation simulation) {
        // noop for basic map
    }

    public void debug(PrintStream s) {
        for (MapLayer layer : getMapLayers()) {
            layer.debug(s);
            s.println();
        }
    }
}