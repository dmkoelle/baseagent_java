package org.baseagent.worldmap;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

import org.baseagent.grid.GridLayer;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;
import org.baseagent.sim.Universe;

public class WorldMap extends SimulationComponent implements Universe {
    private int widthInTiles;
    private int heightInTiles;
    private java.util.Map<String, WorldMapGridLayer> layers;

    public WorldMap(int widthInTiles, int heightInTiles) {
        super();
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.layers = new HashMap<>();
        // create a default layer
        createMapLayer(WorldMapGridLayer.DEFAULT_MAP_LAYER);
    }

    /**
     * Attach an existing Grid as a Map overlay using the grid's default layer name.
     * The returned MapLayer will have the backing Grid attached. Note: you should
     * call setGridBounds(...) on the returned MapLayer to provide geographic
     * anchoring if you want the overlay to be positioned on the MapCanvas.
     */
    public WorldMapGridLayer addGridOverlay(org.baseagent.grid.Grid grid) {
        if (grid == null)
            return addGridOverlay((GridLayer) null);
        // prefer the grid's default layer name if present
        String defaultLayerName = org.baseagent.grid.Grid.DEFAULT_GRID_LAYER;
        GridLayer gl = grid.getGridLayer(defaultLayerName);
        if (gl == null) {
            // create a GridLayer if it doesn't exist
            gl = grid.createGridLayer(defaultLayerName, GridLayer.GridLayerUpdateOption.NO_SWITCH);
        }
        return addGridOverlay(gl);
    }

    /**
     * Attach an existing Grid as a Map overlay with an explicit map-layer name. The
     * MapLayer will be backed by the provided Grid. If this Map is already
     * registered with a Simulation, the Grid will be added to the Simulation so its
     * step/swap behavior runs automatically.
     */
    public WorldMapGridLayer addGridOverlay(String layerName, org.baseagent.grid.Grid grid) {
        // Convenience: locate or create the GridLayer on the grid with the given name
        if (grid == null)
            return addGridOverlay((GridLayer) null);
        if (layerName == null)
            layerName = org.baseagent.grid.Grid.DEFAULT_GRID_LAYER;
        GridLayer gl = grid.getGridLayer(layerName);
        if (gl == null) {
            gl = grid.createGridLayer(layerName, GridLayer.GridLayerUpdateOption.NO_SWITCH);
        }
        return addGridOverlay(layerName, gl);
    }

    /** Primary API: attach a GridLayer as the backing data store for a MapLayer. */
    public WorldMapGridLayer addGridOverlay(GridLayer gridLayer) {
        String layerName = (gridLayer == null) ? null : gridLayer.getLayerName();
        if (layerName == null || layerName.isEmpty())
            layerName = "grid_overlay_" + layers.size();
        return addGridOverlay(layerName, gridLayer);
    }

    /**
     * Attach an existing GridLayer as a Map overlay with an explicit map-layer
     * name.
     */
    public WorldMapGridLayer addGridOverlay(String layerName, GridLayer gridLayer) {
        if (layerName == null)
            layerName = "grid_overlay_" + layers.size();
        WorldMapGridLayer ml = createMapLayer(layerName);
        ml.attachBackingGridLayer(gridLayer);
        // If map already has a simulation, register the parent Grid so it participates
        // in stepping
        if (this.getSimulation() != null && gridLayer != null && gridLayer.getParentGrid() != null) {
            try {
                this.getSimulation().add(gridLayer.getParentGrid());
            } catch (Exception ex) {
                // ignore if grid already added
            }
        }
        return ml;
    }

    /**
     * Attach an existing Grid as a Map overlay and set geographic bounds in one
     * call.
     */
    public WorldMapGridLayer addGridOverlay(String layerName, GridLayer gridLayer, double topLat, double leftLon,
            double bottomLat, double rightLon) {
        WorldMapGridLayer ml = addGridOverlay(layerName, gridLayer);
        if (gridLayer != null && gridLayer.getParentGrid() != null) {
            ml.setGridBounds(topLat, leftLon, bottomLat, rightLon, gridLayer.getParentGrid().getHeightInCells(),
                    gridLayer.getParentGrid().getWidthInCells());
        } else {
            ml.setGridBounds(topLat, leftLon, bottomLat, rightLon, ml.getGridRows(), ml.getGridCols());
        }
        return ml;
    }

    public int getWidthInTiles() {
        return this.widthInTiles;
    }

    public int getHeightInTiles() {
        return this.heightInTiles;
    }

    public WorldMapGridLayer createMapLayer(String name) {
        WorldMapGridLayer layer = new WorldMapGridLayer(name, this);
        addMapLayer(name, layer);
        return layer;
    }

    public void addMapLayer(String name, WorldMapGridLayer layer) {
        this.layers.put(name, layer);
    }

    public WorldMapGridLayer getMapLayer(String name) {
        return layers.get(name);
    }

    public void removeMapLayer(String name) {
        this.layers.remove(name);
    }

    public Collection<WorldMapGridLayer> getMapLayers() {
        return layers.values();
    }

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
        for (WorldMapGridLayer layer : getMapLayers()) {
            layer.debug(s);
            s.println();
        }
    }
}