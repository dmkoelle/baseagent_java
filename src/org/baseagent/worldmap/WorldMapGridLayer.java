package org.baseagent.worldmap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridCell;
import org.baseagent.grid.GridLayer;
import org.baseagent.util.GeoUtils;

/**
 * WorldMap-specific GridLayer backed by the Grid/GridLayer infrastructure.
 * Adds geographic bounds and a generic classification layer and remote lookup
 * so callers can obtain tokens (arbitrary strings) describing what's under
 * the map tiles. The class does not assume any particular token vocabulary.
 */
public class WorldMapGridLayer extends GridLayer<Object> {
    public static final String DEFAULT_MAP_LAYER = "DEFAULT_MAP_LAYER";

    private String layerName;
    private WorldMap parentMap;

    // Geographic bounds for mapping a separate classification grid to lat/lon
    private double topLat, leftLon, bottomLat, rightLon;
    private int rows = 0, cols = 0; // classification grid dimensions (rows x cols)

    // Optional backing grid layer (if attached)
    private GridLayer<?> backingGridLayer = null;

    // Optional classification data [row][col], with row 0 = top row
    private String[][] classificationData = null;

    // Adapter Grid that proxies size queries to the WorldMap so GridLayerStep sizes correctly
    private static class WorldMapGridAdapter extends Grid {
        private final WorldMap parentMap;
        public WorldMapGridAdapter(WorldMap parentMap) {
            super(Math.max(1, parentMap.getWidthInTiles()), Math.max(1, parentMap.getHeightInTiles()));
            this.parentMap = parentMap;
        }

        @Override
        public int getWidthInCells() { return parentMap.getWidthInTiles(); }

        @Override
        public int getHeightInCells() { return parentMap.getHeightInTiles(); }
    }

    public WorldMapGridLayer(WorldMap parentMap, GridLayer.GridLayerUpdateOption updateOption) {
        super(new WorldMapGridAdapter(parentMap), updateOption);
        this.parentMap = parentMap;
    }

    public WorldMapGridLayer(String layerName, WorldMap parentMap) {
        this(parentMap, GridLayer.GridLayerUpdateOption.NEXT_BECOMES_CURRENT);
        this.layerName = layerName;
        parentMap.addMapLayer(layerName, this);
    }

    public String getLayerName() { return this.layerName; }
    public WorldMap getParentMap() { return this.parentMap; }

    public void setGridBounds(double topLat, double leftLon, double bottomLat, double rightLon, int rows, int cols) {
        this.topLat = topLat;
        this.leftLon = leftLon;
        this.bottomLat = bottomLat;
        this.rightLon = rightLon;
        this.rows = rows;
        this.cols = cols;
    }

    public int getGridRows() { return this.rows; }
    public int getGridCols() { return this.cols; }
    public double getTopLat() { return this.topLat; }
    public double getLeftLon() { return this.leftLon; }
    public double getBottomLat() { return this.bottomLat; }
    public double getRightLon() { return this.rightLon; }

    public void attachBackingGridLayer(GridLayer<?> gridLayer) {
        this.backingGridLayer = gridLayer;
        if (gridLayer != null && gridLayer.getParentGrid() != null) {
            Grid parent = gridLayer.getParentGrid();
            this.cols = parent.getWidthInCells();
            this.rows = parent.getHeightInCells();
        }
    }

    public GridLayer<?> getBackingGridLayer() { return this.backingGridLayer; }

    public boolean isGridLayer() { return (rows > 0) && (cols > 0); }

    /**
     * Convert lat/lon to classification-grid cell indices [col,row].
     */
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
        if (col < 0) col = 0; if (col >= cols) col = cols - 1;
        if (row < 0) row = 0; if (row >= rows) row = rows - 1;
        return new int[] { col, row };
    }

    /**
     * Convert a classification-grid cell (col,row) to the geographic lat/lon
     * at the center of that cell. Returns {lat, lon}.
     */
    public double[] cellToLatLon(int col, int row) {
        if (!isGridLayer()) throw new IllegalStateException("Layer is not configured as a grid.");
        if (col < 0) col = 0; if (col >= cols) col = cols - 1;
        if (row < 0) row = 0; if (row >= rows) row = rows - 1;

        double[] topMeters = GeoUtils.latLonToMeters(topLat, leftLon);
        double[] bottomMeters = GeoUtils.latLonToMeters(bottomLat, rightLon);

        double leftX = topMeters[0];
        double rightX = bottomMeters[0];
        double topY = topMeters[1];
        double bottomY = bottomMeters[1];

        double fracX = (col + 0.5) / (double) cols;
        double fracY = (row + 0.5) / (double) rows; // 0 at top, 1 at bottom

        double mx = leftX + fracX * (rightX - leftX);
        double my = topY - fracY * (topY - bottomY);
        return GeoUtils.metersToLatLon(mx, my);
    }

    /**
     * Load a simple classification file where each non-empty line represents a row
     * with tokens separated by commas or whitespace. First line is the top row.
     * (Kept for compatibility; remote service is preferred when available.)
     */
    public void loadClassificationFromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            java.util.List<String[]> rowsList = new java.util.ArrayList<>();
            String line;
            int expectedCols = -1;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split("[,\\s]+");
                for (int i = 0; i < tokens.length; i++) tokens[i] = tokens[i].trim();
                if (expectedCols == -1) expectedCols = tokens.length;
                if (tokens.length != expectedCols) {
                    throw new IOException("Inconsistent column count in classification file: expected " + expectedCols + " but saw " + tokens.length);
                }
                rowsList.add(tokens);
            }
            if (rowsList.isEmpty()) throw new IOException("Empty classification file: " + filename);
            this.rows = rowsList.size();
            this.cols = rowsList.get(0).length;
            this.classificationData = new String[this.rows][this.cols];
            for (int r = 0; r < this.rows; r++) {
                this.classificationData[r] = Arrays.copyOf(rowsList.get(r), this.cols);
            }
        }
    }

    public void setClassificationData(String[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0) throw new IllegalArgumentException("Invalid classification data");
        this.rows = data.length;
        this.cols = data[0].length;
        this.classificationData = new String[this.rows][this.cols];
        for (int r = 0; r < this.rows; r++) {
            if (data[r].length != this.cols) throw new IllegalArgumentException("Inconsistent column counts in classification data");
            this.classificationData[r] = Arrays.copyOf(data[r], this.cols);
        }
    }

    public String getClassificationValue(int col, int row) {
        if (classificationData == null) return null;
        if (row < 0 || row >= rows || col < 0 || col >= cols) return null;
        return classificationData[row][col];
    }

    // Remote classification support
    private final Map<String, String> externalClassificationCache = new ConcurrentHashMap<>();
    private String classificationServiceEndpointTemplate = null; // placeholders: {lat}, {lon}, {z}
    private int classificationServiceZoom = 12;
    private int classificationServiceTimeoutMs = 5000;

    // Function to interpret raw response into a single token. Pluggable by caller.
    // Default: return first whitespace/comma-separated token from response, trimmed.
    private Function<String, String> classificationInterpreter = (raw) -> {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;
        String[] parts = s.split("[,\\s]+", 2);
        return parts[0].trim();
    };

    public void setClassificationServiceEndpointTemplate(String template) { this.classificationServiceEndpointTemplate = template; }
    public void setClassificationServiceZoom(int z) { this.classificationServiceZoom = z; }
    public void setClassificationServiceTimeoutMs(int ms) { this.classificationServiceTimeoutMs = ms; }
    public void setClassificationInterpreter(Function<String, String> interpreter) { if (interpreter != null) this.classificationInterpreter = interpreter; }
    public void clearExternalClassificationCache() { externalClassificationCache.clear(); }

    private String buildClassificationServiceUrl(double lat, double lon, int zoom) {
        if (classificationServiceEndpointTemplate == null) return null;
        String s = classificationServiceEndpointTemplate;
        s = s.replace("{lat}", Double.toString(lat));
        s = s.replace("{lon}", Double.toString(lon));
        s = s.replace("{z}", Integer.toString(zoom));
        return s;
    }

    public String queryClassificationService(double lat, double lon, int zoom) {
        if (classificationServiceEndpointTemplate == null) return null;
        String cacheKey = lat + "," + lon + "," + zoom;
        if (externalClassificationCache.containsKey(cacheKey)) return externalClassificationCache.get(cacheKey);
        String urlStr = buildClassificationServiceUrl(lat, lon, zoom);
        if (urlStr == null) return null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(classificationServiceTimeoutMs);
            conn.setReadTimeout(classificationServiceTimeoutMs);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain, application/json, */*");
            int rc = conn.getResponseCode();
            InputStream is = (rc >= 200 && rc < 300) ? conn.getInputStream() : conn.getErrorStream();
            if (is == null) return null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                String resp = sb.toString().trim();
                externalClassificationCache.put(cacheKey, resp);
                return resp;
            }
        } catch (Exception ex) {
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /**
     * Interpret raw service response into a token using the configured interpreter.
     */
    public String classificationTokenForLatLon(double lat, double lon) {
        String raw = queryClassificationService(lat, lon, classificationServiceZoom);
        if (raw == null) return null;
        return classificationInterpreter.apply(raw);
    }

    public String fetchClassificationForCellRemote(int col, int row) {
        if (!isGridLayer()) return null;
        if (col < 0) col = 0; if (col >= cols) col = cols - 1;
        if (row < 0) row = 0; if (row >= rows) row = rows - 1;
        String key = "cell:" + col + "," + row;
        if (externalClassificationCache.containsKey(key)) return externalClassificationCache.get(key);
        double[] latlon = cellToLatLon(col, row);
        String token = classificationTokenForLatLon(latlon[0], latlon[1]);
        if (token != null) externalClassificationCache.put(key, token);
        return token;
    }

    /**
     * Generic API: obtain a single classification token for the specified classification-grid cell.
     * Resolution of where the token comes from (local table, backing grid, remote service, or
     * this layer's stored value) follows this preference and is transparent to callers.
     */
    public String getClassificationTokenForCell(int col, int row) {
        // 1) local classification data
        String val = getClassificationValue(col, row);
        if (val != null) return val;

        // 2) backing grid layer (if attached)
        if (backingGridLayer != null) {
            try {
                Object o = backingGridLayer.get(col, row);
                if (o != null) return o.toString();
            } catch (Exception ex) { /* ignore */ }
        }

        // 3) remote service
        String remote = fetchClassificationForCellRemote(col, row);
        if (remote != null) return remote;

        // 4) fallback to this layer's stored value (if present)
        try {
            Object o2 = get(col, row);
            if (o2 != null) return o2.toString();
        } catch (Exception ex) { /* ignore */ }

        return null;
    }

    /**
     * Convenience: get classification token at geographic location (lat,lon).
     */
    public String getClassificationTokenForLatLon(double lat, double lon) {
        if (isGridLayer()) {
            int[] cr = latLonToCell(lat, lon);
            return getClassificationTokenForCell(cr[0], cr[1]);
        }
        return classificationTokenForLatLon(lat, lon);
    }

    /**
     * Predicate builder for pathfinding: returns a Predicate<GridCell> that
     * considers a cell traversable when its classification token (as provided
     * by getClassificationTokenForCell) matches one of the allowed tokens
     * (case-insensitive).
     */
    public Predicate<GridCell> traversablePredicateFor(Set<String> allowedTokens) {
        final Set<String> lower = new HashSet<>();
        for (String t : allowedTokens) lower.add(t.toLowerCase());
        return (GridCell cell) -> {
            int col = cell.getCellX();
            int row = cell.getCellY();
            String token = getClassificationTokenForCell(col, row);
            if (token == null) return false;
            return lower.contains(token.toLowerCase());
        };
    }

    public void debug(PrintStream s) {
        s.println(getLayerName());
        for (int y = 0; y < parentMap.getHeightInTiles(); y++) {
            for (int x = 0; x < parentMap.getWidthInTiles(); x++) {
                Object o = get(x, y);
                s.print((o == null) ? "(null)" : o.toString());
                s.print(" ");
            }
            s.println();
        }
    }
}