package org.baseagent.map;

import java.io.PrintStream;
import java.util.Iterator;

import org.baseagent.util.GeoUtils;

public class MapLayer implements Iterable<Object> {
    public static final String DEFAULT_MAP_LAYER = "DEFAULT_MAP_LAYER";

    private String layerName;
    private MapLayerStep current;
    private MapLayerStep next;
    private Map parentMap;

    public enum MapLayerUpdateOption { NEXT_BECOMES_CURRENT, NO_SWITCH };

    private MapLayerUpdateOption updateOption;

    // Optional grid overlay metadata (lat/lon bbox and rows/cols)
    private double topLat, leftLon, bottomLat, rightLon;
    private int rows = 0, cols = 0;

    public void setGridBounds(double topLat, double leftLon, double bottomLat, double rightLon, int rows, int cols) {
        this.topLat = topLat;
        this.leftLon = leftLon;
        this.bottomLat = bottomLat;
        this.rightLon = rightLon;
        this.rows = rows;
        this.cols = cols;
    }

    public boolean isGridLayer() {
        return (rows > 0) && (cols > 0);
    }

    /** Converts lat/lon to cell indices [col,row] within this grid layer. */
    public int[] latLonToCell(double lat, double lon) {
        if (!isGridLayer()) throw new IllegalStateException("Layer is not configured as a grid.");
        double[] topMeters = GeoUtils.latLonToMeters(topLat, leftLon);
        double[] bottomMeters = GeoUtils.latLonToMeters(bottomLat, rightLon);
        double[] ptMeters = GeoUtils.latLonToMeters(lat, lon);

        double leftX = topMeters[0];
        double rightX = bottomMeters[0];
        double topY = topMeters[1];
        double bottomY = bottomMeters[1];

        double fracX = (ptMeters[0] - leftX) / (rightX - leftX);
        double fracY = (topY - ptMeters[1]) / (topY - bottomY); // top -> 0, bottom -> 1
        int col = (int)Math.floor(fracX * cols);
        int row = (int)Math.floor(fracY * rows);
        if (col < 0) col = 0; if (col >= cols) col = cols-1;
        if (row < 0) row = 0; if (row >= rows) row = rows-1;
        return new int[] { col, row };
    }

    public int getGridRows() { return rows; }
    public int getGridCols() { return cols; }
    public double getTopLat() { return topLat; }
    public double getLeftLon() { return leftLon; }
    public double getBottomLat() { return bottomLat; }
    public double getRightLon() { return rightLon; }

    public MapLayer(Map parentMap, MapLayerUpdateOption updateOption) {
        setParentMap(parentMap);
        this.updateOption = updateOption;
    }

    public MapLayer(String layerName, Map parentMap) {
        this(parentMap, MapLayerUpdateOption.NEXT_BECOMES_CURRENT);
        this.layerName = layerName;
        parentMap.addMapLayer(layerName, this);
    }

    public void setParentMap(Map parentMap) {
        this.parentMap = parentMap;
        this.current = new MapLayerStep(parentMap);
        this.next = new MapLayerStep(parentMap);
    }

    public String getLayerName() { return this.layerName; }
    public Map getParentMap() { return this.parentMap; }
    public MapLayerStep current() { return this.current; }
    public MapLayerStep next() { return this.next; }

    protected void switchToNextStep() {
        if (updateOption == MapLayerUpdateOption.NEXT_BECOMES_CURRENT) {
            this.current = this.next;
            this.next = new MapLayerStep(parentMap);
        }
    }

    public void setUpdateOption(MapLayerUpdateOption updateOption) { this.updateOption = updateOption; }
    public MapLayerUpdateOption getUpdateOption() { return this.updateOption; }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            int x = 0, y = 0;
            @Override
            public boolean hasNext() {
                if (x + 1 < parentMap.getWidthInTiles()) return true;
                if (y + 1 < parentMap.getHeightInTiles()) return true;
                return false;
            }
            @Override
            public Object next() {
                x = x + 1;
                if (x > parentMap.getWidthInTiles()) {
                    x = 0; y = y + 1;
                }
                return current.get(x, y);
            }
        };
    }

    // Delegates
    public void fill(Object value) { current.fill(value); }
    public void set(int x, int y, Object value) { getWriteLayer().set(x, y, value); }
    public void clear(int x, int y) { getWriteLayer().clear(x, y); }
    public Object get(int x, int y) { return current.get(x, y); }

    private MapLayerStep getWriteLayer() {
        switch (updateOption) {
            case NEXT_BECOMES_CURRENT: return next;
            case NO_SWITCH: default: return current;
        }
    }

    public void debug(PrintStream s) {
        s.println(getLayerName());
        for (int y=0; y < parentMap.getHeightInTiles(); y++) {
            for (int x=0; x < parentMap.getWidthInTiles(); x++) {
                Object o = get(x,y);
                s.print((o==null)?"(null)":o.toString());
                s.print(" ");
            }
            s.println();
        }
    }

    /**
     * Convert a grid cell (col,row) to the geographic lat/lon at the center of that cell.
     * Requires this layer to be configured as a grid via setGridBounds(...).
     * Returns double[]{lat, lon}.
     */
    public double[] cellToLatLon(int col, int row) {
        if (!isGridLayer()) throw new IllegalStateException("Layer is not configured as a grid.");
        // clamp
        if (col < 0) col = 0; if (col >= cols) col = cols-1;
        if (row < 0) row = 0; if (row >= rows) row = rows-1;

        double[] topMeters = GeoUtils.latLonToMeters(topLat, leftLon);
        double[] bottomMeters = GeoUtils.latLonToMeters(bottomLat, rightLon);

        double leftX = topMeters[0];
        double rightX = bottomMeters[0];
        double topY = topMeters[1];
        double bottomY = bottomMeters[1];

        // fractional position of cell center
        double fracX = (col + 0.5) / (double)cols;
        double fracY = (row + 0.5) / (double)rows; // 0 at top, 1 at bottom

        double mx = leftX + fracX * (rightX - leftX);
        double my = topY - fracY * (topY - bottomY);

        return GeoUtils.metersToLatLon(mx, my);
    }
}