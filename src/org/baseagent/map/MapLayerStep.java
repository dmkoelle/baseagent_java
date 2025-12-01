package org.baseagent.map;

import java.util.ArrayList;
import java.util.List;

public class MapLayerStep {
    List<List<Object>> tiles;
    Map parentMap;

    public MapLayerStep(Map parentMap) {
        this.parentMap = parentMap;
        this.tiles = initNewLayer(parentMap.getWidthInTiles(), parentMap.getHeightInTiles());
    }

    private List<List<Object>> initNewLayer(int width, int height) {
        List<List<Object>> retVal = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            List<Object> row = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                row.add(null);
            }
            retVal.add(row);
        }
        return retVal;
    }

    public void fill(Object value) {
        for (int y = 0; y < parentMap.getHeightInTiles(); y++) {
            for (int x = 0; x < parentMap.getWidthInTiles(); x++) {
                tiles.get(y).set(x, value);
            }
        }
    }

    public void set(int x, int y, Object value) {
        tiles.get(boundY(y)).set(boundX(x), value);
    }

    public Object get(int x, int y) {
        return tiles.get(boundY(y)).get(boundX(x));
    }

    public void clear(int x, int y) { set(x, y, null); }

    private int boundX(int x) {
        if (x < 0) return 0;
        if (x >= parentMap.getWidthInTiles()) return parentMap.getWidthInTiles()-1;
        return x;
    }

    private int boundY(int y) {
        if (y < 0) return 0;
        if (y >= parentMap.getHeightInTiles()) return parentMap.getHeightInTiles()-1;
        return y;
    }
}
